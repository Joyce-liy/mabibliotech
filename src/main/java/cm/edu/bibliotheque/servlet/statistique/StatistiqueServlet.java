package cm.edu.bibliotheque.servlet.statistique;

import cm.edu.bibliotheque.service.AnalyseService; // Changement de service ici
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Year;

@WebServlet("/statistiques")
public class StatistiqueServlet extends HttpServlet {
    // Utilisation du nouveau service dédié à la page d'analyse
    private final AnalyseService analyseService = new AnalyseService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int annee = parseYear(request.getParameter("annee"));
        
        request.setAttribute("pageTitle", "Analyses & Statistiques");
        request.setAttribute("stats", analyseService.getStatsGlobalesAnalyse());
        request.setAttribute("topOuvrages", analyseService.getTopOuvragesPourAnalyse(10)); // Top 10 pour l'analyse
        request.setAttribute("tauxRetard", analyseService.getTauxRetardParCategorie());
        request.setAttribute("evolution", analyseService.getEvolutionMensuelle(annee));
        request.setAttribute("annee", annee);
        
        ServletUtil.forward(request, response, "statistique/analyse.jsp");
    }

    private int parseYear(String value) {
        try {
            return value == null || value.isBlank() ? Year.now().getValue() : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return Year.now().getValue();
        }
    }
}