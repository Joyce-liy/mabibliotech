package cm.edu.bibliotheque.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.enums.StatutEmprunt;
import cm.edu.bibliotheque.enums.TypeMembre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

public class MembreDAO extends GenericDAO<Membre, Long> {
    public MembreDAO() {
        super(Membre.class);
    }

    public Membre findByCarteNumero(String numero) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select m from Membre m where m.carteNumero = :numero", Membre.class)
                    .setParameter("numero", numero)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Membre> findByNomOrPrenom(String query) {
        EntityManager em = getEntityManager();
        String pattern = "%" + query.toLowerCase() + "%";
        try {
            return em.createQuery("""
                    select m from Membre m
                    where lower(m.nom) like :q or lower(m.prenom) like :q or lower(m.carteNumero) like :q
                    order by m.nom, m.prenom
                    """, Membre.class)
                    .setParameter("q", pattern)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Membre> findByTypeMembre(TypeMembre type) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select m from Membre m where m.typeMembre = :type order by m.nom", Membre.class)
                    .setParameter("type", type)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Membre> findActifs() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select m from Membre m where m.actif = true order by m.nom, m.prenom", Membre.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Membre> findBloques() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("""
                    select m from Membre m
                    where m.bloque = true
                    order by m.nom, m.prenom
                    """, Membre.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean hasEmpruntEnCours(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("""
                    select count(e) from Emprunt e
                    where e.membre.id = :membreId and e.statut = :statut
                    """, Long.class)
                    .setParameter("membreId", membreId)
                    .setParameter("statut", StatutEmprunt.EN_COURS)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public boolean hasPenaliteImpayee(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("""
                    select count(p) from Penalite p
                    where p.emprunt.membre.id = :membreId and p.payee = false
                    """, Long.class)
                    .setParameter("membreId", membreId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public boolean carteExpiree(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            LocalDate date = em.createQuery("""
                    select m.dateExpirationCarte from Membre m
                    where m.id = :membreId
                    """, LocalDate.class)
                    .setParameter("membreId", membreId)
                    .getSingleResult();
            return date != null && date.isBefore(LocalDate.now());
        } finally {
            em.close();
        }
    }

    public long countActifs() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select count(m) from Membre m where m.actif = true", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public void desactiver(Long membreId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Membre membre = em.find(Membre.class, membreId);
            if (membre != null) {
                membre.setActif(false);
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

    public void setBloque(Long membreId, boolean bloque) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Membre membre = em.find(Membre.class, membreId);
            if (membre != null) {
                membre.setBloque(bloque);
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

    public BigDecimal calculerTotalImpayes(Long membreId) {
        EntityManager em = getEntityManager();
        try {
            BigDecimal total = em.createQuery("select coalesce(sum(p.montantFcfa),0) from Penalite p where p.payee = false and p.emprunt.membre.id = :membreId", BigDecimal.class)
                    .setParameter("membreId", membreId)
                    .getSingleResult();
            return total == null ? BigDecimal.ZERO : total;
        } finally {
            em.close();
        }
    }
}
