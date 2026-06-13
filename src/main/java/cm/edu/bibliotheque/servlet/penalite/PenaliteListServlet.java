package cm.edu.bibliotheque.servlet.penalite;

import cm.edu.bibliotheque.entity.Penalite;
import cm.edu.bibliotheque.service.PenaliteService;
import cm.edu.bibliotheque.util.PaginationUtil;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/penalites")
public class PenaliteListServlet extends HttpServlet {
    private final PenaliteService penaliteService = new PenaliteService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = PaginationUtil.page(request);
        int size = PaginationUtil.size(request);
        String payee = request.getParameter("payee");

        List<Penalite> penalites = "false".equals(payee)
                ? penaliteService.findImpayees()
                : penaliteService.findAll(page, size);
        long total = "false".equals(payee) ? penalites.size() : penaliteService.count();

        request.setAttribute("pageTitle", "Penalites");
        request.setAttribute("penalites", penalites);
        request.setAttribute("payee", payee);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalItems", total);
        request.setAttribute("totalPages", PaginationUtil.totalPages(total, size));
        ServletUtil.forward(request, response, "penalite/list.jsp");
    }
}
