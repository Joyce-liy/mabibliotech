package cm.edu.bibliotheque.servlet.emprunt;

import cm.edu.bibliotheque.entity.Utilisateur;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.EmpruntService;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.service.OuvrageService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet({"/emprunts/nouveau", "/emprunts/sauvegarder"})
public class EmpruntFormServlet extends HttpServlet {
    private final EmpruntService empruntService = new EmpruntService();
    private final MembreService membreService = new MembreService();
    private final OuvrageService ouvrageService = new OuvrageService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        prepareForm(request);
        request.setAttribute("pageTitle", "Nouvel emprunt");
        ServletUtil.forward(request, response, "emprunt/form.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long membreId = ServletUtil.getLongParameter(request, "membreId");
            Long ouvrageId = ServletUtil.getLongParameter(request, "ouvrageId");
            String dateEmpruntStr = request.getParameter("dateEmprunt");
            LocalDate dateEmprunt = (dateEmpruntStr != null && !dateEmpruntStr.isBlank())
                    ? LocalDate.parse(dateEmpruntStr)
                    : LocalDate.now();

            Utilisateur utilisateur = (Utilisateur) request.getSession().getAttribute("utilisateur");
            Long bibliothId = utilisateur == null ? null : utilisateur.getId();

            empruntService.creerEmprunt(membreId, ouvrageId, bibliothId, dateEmprunt);

            ServletUtil.flash(request, "success", "Emprunt enregistre avec succes.");
            ServletUtil.redirect(request, response, "/emprunts");
        } catch (BusinessException | RuntimeException ex) {
            ServletUtil.flash(request, "danger", ex.getMessage());
            prepareForm(request);
            request.setAttribute("error", ex.getMessage());
            ServletUtil.forward(request, response, "emprunt/form.jsp");
        }
    }

    private void prepareForm(HttpServletRequest request) {
        membreService.evaluerBlocagesAutomatiques();
        request.setAttribute("membres", membreService.findActifs());
        request.setAttribute("ouvrages", ouvrageService.findDisponibles());
    }
}
