package cm.edu.bibliotheque.servlet.transaction;

import cm.edu.bibliotheque.entity.TransactionPaiement;
import cm.edu.bibliotheque.service.TransactionService;
import cm.edu.bibliotheque.util.PaginationUtil;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/transactions")
public class TransactionListServlet extends HttpServlet {
    private final TransactionService transactionService = new TransactionService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = PaginationUtil.page(request);
        int size = PaginationUtil.size(request);
        List<TransactionPaiement> transactions = transactionService.findAll(page, size);
        long total = transactionService.count();

        request.setAttribute("pageTitle", "Historique des paiements");
        request.setAttribute("transactions", transactions);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalItems", total);
        request.setAttribute("totalPages", PaginationUtil.totalPages(total, size));
        ServletUtil.forward(request, response, "transaction/list.jsp");
    }
}
