package cm.edu.bibliotheque.dao;

import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.enums.StatutEmprunt;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.time.LocalDate;
import java.util.List;

public class EmpruntDAO extends GenericDAO<Emprunt, Long> {
    public EmpruntDAO() {
        super(Emprunt.class);
    }

    public Emprunt findByIdWithDetails(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    left join fetch e.bibliothecaire
                    left join fetch e.penalite
                    where e.id = :id
                    """, Emprunt.class)
                    .setParameter("id", id)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Emprunt> findAllWithDetails(int page, int pageSize) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre
                    join fetch e.ouvrage
                    left join fetch e.bibliothecaire
                    order by e.dateEmprunt desc, e.id desc
                    """, Emprunt.class)
                    .setFirstResult((page - 1) * pageSize)
                    .setMaxResults(pageSize)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Emprunt> findByMembre(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre join fetch e.ouvrage
                    where e.membre.id = :membreId
                    order by e.dateEmprunt desc
                    """, Emprunt.class)
                    .setParameter("membreId", membreId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Emprunt> findByOuvrage(Long ouvrageId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre join fetch e.ouvrage
                    where e.ouvrage.id = :ouvrageId
                    order by e.dateEmprunt desc
                    """, Emprunt.class)
                    .setParameter("ouvrageId", ouvrageId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Emprunt> findByStatut(StatutEmprunt statut) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre join fetch e.ouvrage
                    where e.statut = :statut
                    order by e.dateRetourPrevue
                    """, Emprunt.class)
                    .setParameter("statut", statut)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Emprunt> findEnRetard() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre join fetch e.ouvrage
                    where e.dateRetourPrevue < :today and e.statut <> :rendu
                    order by e.dateRetourPrevue
                    """, Emprunt.class)
                    .setParameter("today", LocalDate.now())
                    .setParameter("rendu", StatutEmprunt.RENDU)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Emprunt> findRetourDansDeuxJours() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select e from Emprunt e
                    join fetch e.membre join fetch e.ouvrage
                    where e.dateRetourPrevue = :date and e.statut = :statut
                    """, Emprunt.class)
                    .setParameter("date", LocalDate.now().plusDays(2))
                    .setParameter("statut", StatutEmprunt.EN_COURS)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public int updateStatuts() {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            int updated = em.createQuery("""
                    update Emprunt e set e.statut = :retard
                    where e.dateRetourPrevue < :today and e.statut = :encours
                    """)
                    .setParameter("retard", StatutEmprunt.EN_RETARD)
                    .setParameter("today", LocalDate.now())
                    .setParameter("encours", StatutEmprunt.EN_COURS)
                    .executeUpdate();
            tx.commit();
            return updated;
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }

    public List<Object[]> countByMois(int annee) {
        EntityManager em = getEntityManager();
        try {
            return em.createNativeQuery("""
                    SELECT YEAR(date_emprunt), MONTH(date_emprunt), MONTHNAME(date_emprunt), COUNT(id)
                    FROM emprunt
                    WHERE YEAR(date_emprunt) = ?
                    GROUP BY YEAR(date_emprunt), MONTH(date_emprunt), MONTHNAME(date_emprunt)
                    ORDER BY MONTH(date_emprunt)
                    """)
                    .setParameter(1, annee)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Object[]> tauxRetardParCategorie() {
        EntityManager em = getEntityManager();
        try {
            return em.createNativeQuery("""
                    SELECT
                        COALESCE(o.categorie, 'Non classee') AS categorie,
                        COUNT(e.id) AS total_emprunts,
                        SUM(CASE WHEN e.statut = 'EN_RETARD' OR e.date_retour_reelle > e.date_retour_prevue THEN 1 ELSE 0 END) AS nb_retards,
                        ROUND(SUM(CASE WHEN e.statut = 'EN_RETARD' OR e.date_retour_reelle > e.date_retour_prevue THEN 1 ELSE 0 END) * 100.0 / COUNT(e.id), 2) AS taux_retard_pct
                    FROM ouvrage o
                    JOIN emprunt e ON o.id = e.ouvrage_id
                    GROUP BY COALESCE(o.categorie, 'Non classee')
                    ORDER BY taux_retard_pct DESC
                    """)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long countByStatut(StatutEmprunt statut) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select count(e) from Emprunt e where e.statut = :statut", Long.class)
                    .setParameter("statut", statut)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
