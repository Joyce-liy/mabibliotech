package cm.edu.bibliotheque.servlet.membre;

import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/membres/supprimer/*")
public class MembreDeleteServlet extends HttpServlet {
    private final MembreService membreService = new MembreService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            membreService.supprimer(ServletUtil.getPathId(request));
            ServletUtil.flash(request, "success", "Membre desactive.");
        } catch (BusinessException | RuntimeException ex) {
            ServletUtil.flash(request, "danger", ex.getMessage());
        }
        ServletUtil.redirect(request, response, "/membres");
    }
}
