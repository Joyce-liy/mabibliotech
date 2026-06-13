package cm.edu.bibliotheque.service;

import cm.edu.bibliotheque.dao.EmpruntDAO;
import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.entity.Ouvrage;
import cm.edu.bibliotheque.entity.Penalite;
import cm.edu.bibliotheque.entity.Utilisateur;
import cm.edu.bibliotheque.enums.StatutEmprunt;
import cm.edu.bibliotheque.enums.TypeMembre;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmpruntService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmpruntService.class);
    private static final BigDecimal TARIF_JOURNALIER = new BigDecimal("50.00");

    private final EmpruntDAO empruntDAO = new EmpruntDAO();
    private final BlocageService blocageService = new BlocageService();

    // PAR CECI :
    public LocalDate calculerDateRetourPrevue(TypeMembre type, LocalDate dateEmprunt) {
        switch (type) {
            case ENSEIGNANT: return dateEmprunt.plusDays(30);
            case EXTERNE:    return dateEmprunt.plusDays(7);
            default:         return dateEmprunt.plusDays(14);
        }
    }

    // Méthode modifiée pour accepter la date d'emprunt personnalisée (pour vos tests)
    public Emprunt creerEmprunt(Long membreId, Long ouvrageId, Long bibliothId, LocalDate dateEmprunt) throws BusinessException {
        if (membreId == null) {
            throw new BusinessException("Membre introuvable.");
        }
        BlocageService.Evaluation evaluation = blocageService.evaluerEtMettreAJour(membreId);
        if (evaluation.isBloque()) {
            throw new BusinessException("Emprunt impossible: membre bloque (" + evaluation.getRaison() + ").");
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Membre membre = em.find(Membre.class, membreId);
            Ouvrage ouvrage = em.find(Ouvrage.class, ouvrageId);
            Utilisateur bibliothecaire = bibliothId == null ? null : em.find(Utilisateur.class, bibliothId);

            validateCreation(em, membre, ouvrage);

            ouvrage.setExemplairesDispo(ouvrage.getExemplairesDispo() - 1);

            Emprunt emprunt = new Emprunt();
            emprunt.setMembre(membre);
            emprunt.setOuvrage(ouvrage);
            emprunt.setBibliothecaire(bibliothecaire);
            // Utilisation de la date reçue du formulaire au lieu de LocalDate.now()
            emprunt.setDateEmprunt(dateEmprunt);
            emprunt.setDateRetourPrevue(calculerDateRetourPrevue(membre.getTypeMembre(), dateEmprunt));
            emprunt.setStatut(StatutEmprunt.EN_COURS);

            em.persist(emprunt);
            tx.commit();
            LOGGER.info("Emprunt cree avec date personnalisee {}: membre={}, ouvrage={}", dateEmprunt, membreId, ouvrageId);
            return emprunt;
        } catch (BusinessException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new BusinessException("Impossible de creer l'emprunt: " + ex.getMessage());
        } finally {
            em.close();
        }
    }

    public void enregistrerRetour(Long empruntId) throws BusinessException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Emprunt emprunt = em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    left join fetch e.penalite
                    where e.id = :id
                    """, Emprunt.class)
                    .setParameter("id", empruntId)
                    .getSingleResult();

            if (StatutEmprunt.RENDU.equals(emprunt.getStatut())) {
                throw new BusinessException("Cet emprunt est deja marque comme rendu.");
            }

            LocalDate today = LocalDate.now();
            emprunt.setDateRetourReelle(today);
            emprunt.setStatut(StatutEmprunt.RENDU);

            Ouvrage ouvrage = emprunt.getOuvrage();
            int total = ouvrage.getExemplairesTotal() == null ? 0 : ouvrage.getExemplairesTotal();
            int dispo = ouvrage.getExemplairesDispo() == null ? 0 : ouvrage.getExemplairesDispo();
            ouvrage.setExemplairesDispo(Math.min(total, dispo + 1));

            if (today.isAfter(emprunt.getDateRetourPrevue()) && emprunt.getPenalite() == null) {
                long jours = ChronoUnit.DAYS.between(emprunt.getDateRetourPrevue(), today);
                Penalite penalite = new Penalite();
                penalite.setEmprunt(emprunt);
                penalite.setJoursRetard((int) jours);
                penalite.setMontantFcfa(TARIF_JOURNALIER.multiply(BigDecimal.valueOf(jours)));
                penalite.setPayee(false);
                penalite.setDateCalcul(today);
                emprunt.setPenalite(penalite);
                em.persist(penalite);
            }

            blocageService.evaluerEtMettreAJour(em, emprunt.getMembre());

            tx.commit();
            LOGGER.info("Retour enregistre pour l'emprunt {}", empruntId);
        } catch (BusinessException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new BusinessException("Impossible d'enregistrer le retour: " + ex.getMessage());
        } finally {
            em.close();
        }
    }

    public int mettreAJourStatuts() {
        int updated = empruntDAO.updateStatuts();
        blocageService.evaluerTous();
        return updated;
    }

    public Emprunt findByIdWithDetails(Long id) {
        return empruntDAO.findByIdWithDetails(id);
    }

    public List<Emprunt> findAllWithDetails(int page, int pageSize) {
        return empruntDAO.findAllWithDetails(page, pageSize);
    }

    public List<Emprunt> findByStatut(StatutEmprunt statut) {
        return empruntDAO.findByStatut(statut);
    }

    public List<Emprunt> findEnRetard() {
        return empruntDAO.findEnRetard();
    }

    public long count() {
        return empruntDAO.count();
    }

    public long countByStatut(StatutEmprunt statut) {
        return empruntDAO.countByStatut(statut);
    }

    private void validateCreation(EntityManager em, Membre membre, Ouvrage ouvrage) throws BusinessException {
        if (membre == null) {
            throw new BusinessException("Membre introuvable.");
        }
        if (ouvrage == null) {
            throw new BusinessException("Ouvrage introuvable.");
        }
        if (!Boolean.TRUE.equals(membre.getActif())) {
            throw new BusinessException("Le membre est inactif.");
        }
        if (membre.getDateExpirationCarte() != null && membre.getDateExpirationCarte().isBefore(LocalDate.now())) {
            throw new BusinessException("La carte du membre est expiree.");
        }
        if (ouvrage.getExemplairesDispo() == null || ouvrage.getExemplairesDispo() <= 0) {
            throw new BusinessException("Aucun exemplaire disponible pour cet ouvrage.");
        }
        if (Boolean.TRUE.equals(membre.getBloque())) {
            throw new BusinessException("Emprunt impossible: membre bloque.");
        }
    }
}
