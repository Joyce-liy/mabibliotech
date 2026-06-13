package cm.edu.bibliotheque.dao;

import cm.edu.bibliotheque.entity.Utilisateur;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

public class UtilisateurDAO extends GenericDAO<Utilisateur, Long> {
    public UtilisateurDAO() {
        super(Utilisateur.class);
    }

    public Utilisateur findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("select u from Utilisateur u where lower(u.email) = lower(:email)", Utilisateur.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } finally {
            em.close();
        }
    }
}
