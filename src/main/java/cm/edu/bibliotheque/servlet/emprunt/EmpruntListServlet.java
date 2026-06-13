package cm.edu.bibliotheque.servlet.emprunt;

import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.enums.StatutEmprunt;
import cm.edu.bibliotheque.service.EmpruntService;
import cm.edu.bibliotheque.util.PaginationUtil;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/emprunts")
public class EmpruntListServlet extends HttpServlet {
    private final EmpruntService empruntService = new EmpruntService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        empruntService.mettreAJourStatuts();
        int page = PaginationUtil.page(request);
        int size = PaginationUtil.size(request);
        StatutEmprunt statut = parseStatut(request.getParameter("statut"));

        List<Emprunt> emprunts;
        long total;
        if (statut != null) {
            emprunts = empruntService.findByStatut(statut);
            total = emprunts.size();
        } else {
            emprunts = empruntService.findAllWithDetails(page, size);
            total = empruntService.count();
        }

        request.setAttribute("pageTitle", "Emprunts");
        request.setAttribute("emprunts", emprunts);
        request.setAttribute("statuts", StatutEmprunt.values());
        request.setAttribute("statut", statut);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalItems", total);
        request.setAttribute("totalPages", PaginationUtil.totalPages(total, size));
        ServletUtil.forward(request, response, "emprunt/list.jsp");
    }

    private StatutEmprunt parseStatut(String value) {
        try {
            return value == null || value.isBlank() ? null : StatutEmprunt.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
