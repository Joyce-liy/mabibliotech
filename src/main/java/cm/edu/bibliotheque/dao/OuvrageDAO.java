package cm.edu.bibliotheque.dao;

import cm.edu.bibliotheque.enums.StatutEmprunt;
import cm.edu.bibliotheque.entity.Ouvrage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import java.util.List;

public class OuvrageDAO extends GenericDAO<Ouvrage, Long> {
    public OuvrageDAO() {
        super(Ouvrage.class);
    }

    public Ouvrage findByIsbn(String isbn) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select o from Ouvrage o where o.isbn = :isbn", Ouvrage.class)
                    .setParameter("isbn", isbn)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Ouvrage> findByTitreContaining(String titre) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select o from Ouvrage o where lower(o.titre) like :titre order by o.titre", Ouvrage.class)
                    .setParameter("titre", "%" + titre.toLowerCase() + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ouvrage> findByAuteur(String auteur) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select o from Ouvrage o where lower(o.auteur) like :auteur order by o.titre", Ouvrage.class)
                    .setParameter("auteur", "%" + auteur.toLowerCase() + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ouvrage> findByCategorie(String categorie) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select o from Ouvrage o where o.categorie = :categorie order by o.titre", Ouvrage.class)
                    .setParameter("categorie", categorie)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ouvrage> findDisponibles() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select o from Ouvrage o where o.exemplairesDispo > 0 order by o.titre", Ouvrage.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void decrementExemplaires(Long ouvrageId) {
        executeStockUpdate(ouvrageId, -1);
    }

    public void incrementExemplaires(Long ouvrageId) {
        executeStockUpdate(ouvrageId, 1);
    }

    public List<Ouvrage> searchFullText(String keyword) {
        EntityManager em = getEntityManager();
        String pattern = "%" + keyword.toLowerCase() + "%";
        try {
            return em.createQuery("""
                    select o from Ouvrage o
                    where lower(o.titre) like :q
                       or lower(o.auteur) like :q
                       or lower(coalesce(o.isbn, '')) like :q
                       or lower(coalesce(o.categorie, '')) like :q
                    order by o.titre
                    """, Ouvrage.class)
                    .setParameter("q", pattern)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Object[]> findTopEmpruntes(int limit) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select o.titre, o.auteur, o.categorie, count(e.id)
                    from Ouvrage o join Emprunt e on e.ouvrage = o
                    group by o.id, o.titre, o.auteur, o.categorie
                    order by count(e.id) desc
                    """, Object[].class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<String> findCategories() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select distinct o.categorie from Ouvrage o
                    where o.categorie is not null and o.categorie <> ''
                    order by o.categorie
                    """, String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean hasEmpruntEnCours(Long ouvrageId) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("""
                    select count(e) from Emprunt e
                    where e.ouvrage.id = :ouvrageId and e.statut = :statut
                    """, Long.class)
                    .setParameter("ouvrageId", ouvrageId)
                    .setParameter("statut", StatutEmprunt.EN_COURS)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    private void executeStockUpdate(Long ouvrageId, int delta) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Ouvrage ouvrage = em.find(Ouvrage.class, ouvrageId);
            if (ouvrage != null) {
                int total = ouvrage.getExemplairesTotal() == null ? 0 : ouvrage.getExemplairesTotal();
                int dispo = ouvrage.getExemplairesDispo() == null ? 0 : ouvrage.getExemplairesDispo();
                int next = Math.max(0, Math.min(total, dispo + delta));
                ouvrage.setExemplairesDispo(next);
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
}
