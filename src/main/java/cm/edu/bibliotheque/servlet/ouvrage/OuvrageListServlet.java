package cm.edu.bibliotheque.servlet.ouvrage;

import cm.edu.bibliotheque.entity.Ouvrage;
import cm.edu.bibliotheque.service.OuvrageService;
import cm.edu.bibliotheque.util.PaginationUtil;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/ouvrages")
public class OuvrageListServlet extends HttpServlet {
    private final OuvrageService ouvrageService = new OuvrageService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = PaginationUtil.page(request);
        int size = PaginationUtil.size(request);
        String query = request.getParameter("q");
        String categorie = request.getParameter("categorie");

        List<Ouvrage> ouvrages;
        long total;
        if ((query != null && !query.isBlank()) || (categorie != null && !categorie.isBlank())) {
            ouvrages = ouvrageService.search(query, categorie);
            total = ouvrages.size();
        } else {
            ouvrages = ouvrageService.findAll(page, size);
            total = ouvrageService.count();
        }

        request.setAttribute("pageTitle", "Ouvrages");
        request.setAttribute("ouvrages", ouvrages);
        request.setAttribute("categories", ouvrageService.findCategories());
        request.setAttribute("q", query);
        request.setAttribute("categorie", categorie);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", PaginationUtil.totalPages(total, size));
        ServletUtil.forward(request, response, "ouvrage/list.jsp");
    }
}
