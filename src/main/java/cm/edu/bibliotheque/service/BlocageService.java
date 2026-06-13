package cm.edu.bibliotheque.service;

import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.enums.StatutEmprunt;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.util.AppConfig;
import cm.edu.bibliotheque.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlocageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlocageService.class);
    private static final String RETARD_MAX_JOURS_KEY = "blocage.retard.maxJours";
    private static final String SEUIL_PENALITES_KEY = "blocage.penalites.seuilFcfa";

    private final int retardMaxJours;
    private final BigDecimal seuilPenalitesFcfa;

    public BlocageService() {
        this.retardMaxJours = AppConfig.getInt(RETARD_MAX_JOURS_KEY, 30);
        this.seuilPenalitesFcfa = AppConfig.getBigDecimal(SEUIL_PENALITES_KEY, new BigDecimal("5000"));
    }

    public Evaluation evaluerSituation(Long membreId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Membre membre = em.find(Membre.class, membreId);
            return evaluer(em, membre);
        } finally {
            em.close();
        }
    }

    public Evaluation evaluerEtMettreAJour(Long membreId) throws BusinessException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Membre membre = em.find(Membre.class, membreId);
            if (membre == null) {
                throw new BusinessException("Membre introuvable.");
            }
            Evaluation evaluation = evaluerEtMettreAJour(em, membre);
            tx.commit();
            return evaluation;
        } catch (BusinessException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public Evaluation evaluerEtMettreAJour(EntityManager em, Membre membre) {
        Evaluation evaluation = evaluer(em, membre);
        appliquerChangement(membre, evaluation);
        return evaluation;
    }

    public int evaluerTous() {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        int changements = 0;
        try {
            tx.begin();
            List<Membre> membres = em.createQuery("""
                    select m from Membre m
                    where m.actif = true
                    """, Membre.class)
                    .getResultList();
            for (Membre membre : membres) {
                Evaluation evaluation = evaluer(em, membre);
                if (appliquerChangement(membre, evaluation)) {
                    changements++;
                }
            }
            tx.commit();
            return changements;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void debloquerManuellement(Long membreId) throws BusinessException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Membre membre = em.find(Membre.class, membreId);
            if (membre == null) {
                throw new BusinessException("Membre introuvable.");
            }
            if (Boolean.TRUE.equals(membre.getBloque())) {
                membre.setBloque(false);
                LOGGER.info("Deblocage manuel du membre {} ({})", membre.getId(), membre.getCarteNumero());
            }
            tx.commit();
        } catch (BusinessException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public int getRetardMaxJours() {
        return retardMaxJours;
    }

    public BigDecimal getSeuilPenalitesFcfa() {
        return seuilPenalitesFcfa;
    }

    private Evaluation evaluer(EntityManager em, Membre membre) {
        if (membre == null || membre.getId() == null) {
            return Evaluation.nonBloque(BigDecimal.ZERO, 0);
        }

        LocalDate limiteRetard = LocalDate.now().minusDays(retardMaxJours);
        long empruntsTresEnRetard = em.createQuery("""
                select count(e) from Emprunt e
                where e.membre.id = :membreId
                  and e.statut <> :rendu
                  and e.dateRetourPrevue < :limiteRetard
                """, Long.class)
                .setParameter("membreId", membre.getId())
                .setParameter("rendu", StatutEmprunt.RENDU)
                .setParameter("limiteRetard", limiteRetard)
                .getSingleResult();

        BigDecimal totalImpayes = em.createQuery("""
                select coalesce(sum(p.montantFcfa), 0)
                from Penalite p
                where p.payee = false and p.emprunt.membre.id = :membreId
                """, BigDecimal.class)
                .setParameter("membreId", membre.getId())
                .getSingleResult();
        if (totalImpayes == null) {
            totalImpayes = BigDecimal.ZERO;
        }

        boolean retardBloquant = empruntsTresEnRetard > 0;
        boolean penalitesBloquantes = totalImpayes.compareTo(seuilPenalitesFcfa) > 0;
        if (retardBloquant || penalitesBloquantes) {
            StringBuilder raison = new StringBuilder();
            if (retardBloquant) {
                raison.append("retard non rendu de plus de ")
                        .append(retardMaxJours)
                        .append(" jours");
            }
            if (penalitesBloquantes) {
                if (raison.length() > 0) {
                    raison.append(" et ");
                }
                raison.append("penalites impayees de ")
                        .append(totalImpayes)
                        .append(" FCFA superieures au seuil de ")
                        .append(seuilPenalitesFcfa)
                        .append(" FCFA");
            }
            return new Evaluation(true, raison.toString(), totalImpayes, empruntsTresEnRetard);
        }

        return Evaluation.nonBloque(totalImpayes, empruntsTresEnRetard);
    }

    private boolean appliquerChangement(Membre membre, Evaluation evaluation) {
        boolean etatActuel = Boolean.TRUE.equals(membre.getBloque());
        if (etatActuel == evaluation.isBloque()) {
            return false;
        }

        membre.setBloque(evaluation.isBloque());
        if (evaluation.isBloque()) {
            LOGGER.warn("Blocage automatique du membre {} ({}): {}",
                    membre.getId(), membre.getCarteNumero(), evaluation.getRaison());
        } else {
            LOGGER.info("Deblocage automatique du membre {} ({})",
                    membre.getId(), membre.getCarteNumero());
        }
        return true;
    }

    public static final class Evaluation {
        private final boolean bloque;
        private final String raison;
        private final BigDecimal totalPenalitesImpayees;
        private final long empruntsTresEnRetard;

        private Evaluation(boolean bloque, String raison, BigDecimal totalPenalitesImpayees,
                           long empruntsTresEnRetard) {
            this.bloque = bloque;
            this.raison = raison;
            this.totalPenalitesImpayees = totalPenalitesImpayees;
            this.empruntsTresEnRetard = empruntsTresEnRetard;
        }

        public static Evaluation nonBloque(BigDecimal totalPenalitesImpayees, long empruntsTresEnRetard) {
            return new Evaluation(false, "Situation reguliere", totalPenalitesImpayees, empruntsTresEnRetard);
        }

        public boolean isBloque() {
            return bloque;
        }

        public String getRaison() {
            return raison;
        }

        public BigDecimal getTotalPenalitesImpayees() {
            return totalPenalitesImpayees;
        }

        public long getEmpruntsTresEnRetard() {
            return empruntsTresEnRetard;
        }
    }
}
