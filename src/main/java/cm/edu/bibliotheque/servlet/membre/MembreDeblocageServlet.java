package cm.edu.bibliotheque.servlet.membre;

import cm.edu.bibliotheque.entity.Utilisateur;
import cm.edu.bibliotheque.enums.RoleUtilisateur;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/membres/debloquer/*")
public class MembreDeblocageServlet extends HttpServlet {
    private final MembreService membreService = new MembreService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            membreService.debloquerManuellement(ServletUtil.getPathId(request));
            ServletUtil.flash(request, "success", "Membre debloque manuellement.");
        } catch (BusinessException | RuntimeException ex) {
            ServletUtil.flash(request, "danger", ex.getMessage());
        }
        ServletUtil.redirect(request, response, "/membres");
    }

    private boolean isAdmin(HttpServletRequest request) {
        Utilisateur utilisateur = (Utilisateur) request.getSession().getAttribute("utilisateur");
        return utilisateur != null && RoleUtilisateur.ADMIN.equals(utilisateur.getRole());
    }
}
