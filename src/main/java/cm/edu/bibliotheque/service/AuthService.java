package cm.edu.bibliotheque.service;

import cm.edu.bibliotheque.dao.UtilisateurDAO;
import cm.edu.bibliotheque.entity.Utilisateur;
import cm.edu.bibliotheque.enums.RoleUtilisateur;
import cm.edu.bibliotheque.util.BcryptUtil;

public class AuthService {
    private static final String DEFAULT_EMAIL = "admin@bibliotheque.univ.cm";
    private static final String DEFAULT_PASSWORD = "admin123";

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public Utilisateur authenticate(String email, String password) {
        Utilisateur utilisateur = utilisateurDAO.findByEmail(email);
        if (utilisateur == null || !Boolean.TRUE.equals(utilisateur.getActif())) {
            return null;
        }
        return BcryptUtil.matches(password, utilisateur.getMotDePasse()) ? utilisateur : null;
    }

    public void ensureDefaultAdmin() {
        if (utilisateurDAO.count() > 0) {
            return;
        }
        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setPrenom("Systeme");
        admin.setEmail(DEFAULT_EMAIL);
        admin.setMotDePasse(BcryptUtil.hash(DEFAULT_PASSWORD));
        admin.setRole(RoleUtilisateur.ADMIN);
        admin.setActif(true);
        utilisateurDAO.save(admin);
    }
}
