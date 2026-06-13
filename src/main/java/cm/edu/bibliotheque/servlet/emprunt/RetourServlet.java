package cm.edu.bibliotheque.servlet.emprunt;

import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.EmpruntService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@WebServlet("/emprunts/retour/*")
public class RetourServlet extends HttpServlet {
    private final EmpruntService empruntService = new EmpruntService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Emprunt emprunt = empruntService.findByIdWithDetails(ServletUtil.getPathId(request));
        if (emprunt == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        long joursRetard = LocalDate.now().isAfter(emprunt.getDateRetourPrevue())
                ? ChronoUnit.DAYS.between(emprunt.getDateRetourPrevue(), LocalDate.now())
                : 0;
        request.setAttribute("pageTitle", "Retour d'ouvrage");
        request.setAttribute("emprunt", emprunt);
        request.setAttribute("joursRetard", joursRetard);
        request.setAttribute("montantPenalite", joursRetard * 50);
        ServletUtil.forward(request, response, "emprunt/retour.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            empruntService.enregistrerRetour(ServletUtil.getPathId(request));
            ServletUtil.flash(request, "success", "Retour enregistre.");
        } catch (BusinessException | RuntimeException ex) {
            ServletUtil.flash(request, "danger", ex.getMessage());
        }
        ServletUtil.redirect(request, response, "/emprunts");
    }
}
