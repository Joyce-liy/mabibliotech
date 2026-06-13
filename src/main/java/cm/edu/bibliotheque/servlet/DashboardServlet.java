package cm.edu.bibliotheque.servlet;

import cm.edu.bibliotheque.service.EmpruntService;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.service.StatistiqueService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Year;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private final EmpruntService empruntService = new EmpruntService();
    private final MembreService membreService = new MembreService();
    private final StatistiqueService statistiqueService = new StatistiqueService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        empruntService.mettreAJourStatuts();
        int annee = Year.now().getValue();
        request.setAttribute("pageTitle", "Tableau de bord");
        request.setAttribute("stats", statistiqueService.getDashboardStats());
        request.setAttribute("retards", empruntService.findEnRetard());
        request.setAttribute("membresBloques", membreService.findBloques());
        request.setAttribute("topOuvrages", statistiqueService.getTopOuvragesEmpruntes(5));
        request.setAttribute("evolution", statistiqueService.getEvolutionMensuelle(annee));
        ServletUtil.forward(request, response, "statistique/dashboard.jsp");
    }
}
