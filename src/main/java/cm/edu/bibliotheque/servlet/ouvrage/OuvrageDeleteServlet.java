package cm.edu.bibliotheque.servlet.ouvrage;

import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.OuvrageService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/ouvrages/supprimer/*")
public class OuvrageDeleteServlet extends HttpServlet {
    private final OuvrageService ouvrageService = new OuvrageService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            ouvrageService.supprimer(ServletUtil.getPathId(request));
            ServletUtil.flash(request, "success", "Ouvrage supprime.");
        } catch (BusinessException | RuntimeException ex) {
            ServletUtil.flash(request, "danger", ex.getMessage());
        }
        ServletUtil.redirect(request, response, "/ouvrages");
    }
}
