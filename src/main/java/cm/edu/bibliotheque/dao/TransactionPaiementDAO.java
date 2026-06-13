package cm.edu.bibliotheque.dao;

import java.util.List;

import cm.edu.bibliotheque.entity.TransactionPaiement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class TransactionPaiementDAO extends GenericDAO<TransactionPaiement, Long> {

    public TransactionPaiementDAO() {
        super(TransactionPaiement.class);
    }

    public TransactionPaiement save(TransactionPaiement t) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(t);
            tx.commit();
            return t;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<TransactionPaiement> findByMembre(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select t from TransactionPaiement t
                    join fetch t.membre m
                    left join fetch t.penalite p
                    left join fetch p.emprunt e
                    left join fetch e.ouvrage
                    where m.id = :membreId
                    order by t.datePaiement desc
                    """, TransactionPaiement.class)
                    .setParameter("membreId", membreId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<TransactionPaiement> findAllWithDetails(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select t from TransactionPaiement t
                    join fetch t.membre m
                    left join fetch t.penalite p
                    left join fetch p.emprunt e
                    left join fetch e.ouvrage
                    order by t.datePaiement desc, t.id desc
                    """, TransactionPaiement.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
