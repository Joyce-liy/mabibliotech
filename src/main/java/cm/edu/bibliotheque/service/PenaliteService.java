package cm.edu.bibliotheque.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import cm.edu.bibliotheque.dao.PenaliteDAO;
import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.entity.Penalite;
import cm.edu.bibliotheque.entity.TransactionPaiement;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.util.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class PenaliteService {
    private static final BigDecimal TARIF_JOURNALIER = new BigDecimal("50.00");

    private final PenaliteDAO penaliteDAO = new PenaliteDAO();
    private final BlocageService blocageService = new BlocageService();

    public Penalite calculerEtSauvegarderPenalite(Emprunt emprunt) {
        if (emprunt == null || emprunt.getId() == null) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Emprunt managed = em.find(Emprunt.class, emprunt.getId());
            Penalite existing = penaliteDAO.findByEmprunt(emprunt.getId());
            if (managed == null || existing != null) {
                tx.commit();
                return existing;
            }

            LocalDate retour = managed.getDateRetourReelle() == null ? LocalDate.now() : managed.getDateRetourReelle();
            if (!retour.isAfter(managed.getDateRetourPrevue())) {
                tx.commit();
                return null;
            }

            long jours = ChronoUnit.DAYS.between(managed.getDateRetourPrevue(), retour);
            Penalite penalite = new Penalite();
            penalite.setEmprunt(managed);
            penalite.setJoursRetard((int) jours);
            penalite.setMontantFcfa(TARIF_JOURNALIER.multiply(BigDecimal.valueOf(jours)));
            penalite.setPayee(false);
            penalite.setDateCalcul(LocalDate.now());
            em.persist(penalite);
            tx.commit();
            return penalite;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public void enregistrerPaiement(Long penaliteId, BigDecimal montant, String moyen, String reference)
            throws BusinessException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Penalite penalite = em.createQuery("""
                    select p from Penalite p
                    join fetch p.emprunt e
                    join fetch e.membre
                    where p.id = :penaliteId
                    """, Penalite.class)
                    .setParameter("penaliteId", penaliteId)
                    .getSingleResult();
            if (Boolean.TRUE.equals(penalite.getPayee())) {
                throw new BusinessException("Cette penalite est deja payee.");
            }

            BigDecimal montantRegle = montant == null ? penalite.getMontantFcfa() : montant;
            if (montantRegle == null || montantRegle.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BusinessException("Le montant du paiement est invalide.");
            }
            String moyenPaiement = moyen == null || moyen.isBlank() ? "ESPECES" : moyen.trim();
            String referencePaiement = reference == null || reference.isBlank()
                    ? genererReference(penaliteId)
                    : reference.trim();

            if (montant != null) {
                penalite.setMontantFcfa(montantRegle);
            }
            penalite.setPayee(true);
            Membre membre = penalite.getEmprunt().getMembre();

            TransactionPaiement transaction = new TransactionPaiement();
            transaction.setMembre(membre);
            transaction.setPenalite(penalite);
            transaction.setMontant(montantRegle);
            transaction.setMoyen(moyenPaiement);
            transaction.setReference(referencePaiement);
            em.persist(transaction);

            blocageService.evaluerEtMettreAJour(em, membre);

            tx.commit();
        } catch (NoResultException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new BusinessException("Penalite introuvable.");
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

    private String genererReference(Long penaliteId) {
        return "REC-" + penaliteId + "-" + java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    }

    public List<Penalite> findAll(int page, int pageSize) {
        return penaliteDAO.findWithDetails(page, pageSize);
    }

    public List<Penalite> findImpayees() {
        return penaliteDAO.findImpayees();
    }

    public long count() {
        return penaliteDAO.count();
    }

    public long countImpayees() {
        return penaliteDAO.countImpayees();
    }

    public BigDecimal sumImpayees() {
        return penaliteDAO.sumImpayees();
    }
}
