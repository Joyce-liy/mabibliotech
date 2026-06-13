package cm.edu.bibliotheque.dao;

import java.math.BigDecimal;
import java.util.List;

import cm.edu.bibliotheque.entity.Penalite;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class PenaliteDAO extends GenericDAO<Penalite, Long> {
    public PenaliteDAO() {
        super(Penalite.class);
    }

    public Penalite findByEmprunt(Long empruntId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select p from Penalite p
                    join fetch p.emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    where e.id = :empruntId
                    """, Penalite.class)
                    .setParameter("empruntId", empruntId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public Penalite findByIdWithDetails(Long penaliteId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select p from Penalite p
                    join fetch p.emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    where p.id = :penaliteId
                    """, Penalite.class)
                    .setParameter("penaliteId", penaliteId)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Penalite> findWithDetails(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select p from Penalite p
                    join fetch p.emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    order by p.dateCalcul desc, p.id desc
                    """, Penalite.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Penalite> findImpayees() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select p from Penalite p
                    join fetch p.emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    where p.payee = false
                    order by p.dateCalcul desc
                    """, Penalite.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Penalite> findImpayeesByMembre(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select p from Penalite p
                    join fetch p.emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    where p.payee = false and e.membre.id = :membreId
                    """, Penalite.class)
                    .setParameter("membreId", membreId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void marquerPayee(Long penaliteId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Penalite penalite = em.find(Penalite.class, penaliteId);
            if (penalite != null) {
                penalite.setPayee(true);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public BigDecimal calculerTotalImpayesByMembre(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            BigDecimal total = em.createQuery("""
                    select coalesce(sum(p.montantFcfa), 0)
                    from Penalite p
                    where p.payee = false and p.emprunt.membre.id = :membreId
                    """, BigDecimal.class)
                    .setParameter("membreId", membreId)
                    .getSingleResult();
            return total == null ? BigDecimal.ZERO : total;
        } finally {
            em.close();
        }
    }

    public long countImpayees() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select count(p) from Penalite p where p.payee = false", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public BigDecimal sumImpayees() {
        EntityManager em = getEntityManager();
        try {
            BigDecimal total = em.createQuery("""
                    select coalesce(sum(p.montantFcfa), 0)
                    from Penalite p where p.payee = false
                    """, BigDecimal.class)
                    .getSingleResult();
            return total == null ? BigDecimal.ZERO : total;
        } finally {
            em.close();
        }
    }
}
