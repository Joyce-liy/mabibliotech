package cm.edu.bibliotheque.servlet.membre;

import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.service.TransactionService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/membres/transactions/*")
public class MembreTransactionsServlet extends HttpServlet {
    private final TransactionService transactionService = new TransactionService();
    private final MembreService membreService = new MembreService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long membreId = ServletUtil.getPathId(request);
        if (membreId == null) {
            ServletUtil.flash(request, "danger", "Membre introuvable.");
            ServletUtil.redirect(request, response, "/membres");
            return;
        }
        Membre membre = membreService.findById(membreId);
        if (membre == null) {
            ServletUtil.flash(request, "danger", "Membre introuvable.");
            ServletUtil.redirect(request, response, "/membres");
            return;
        }
        request.setAttribute("pageTitle", "Historique des paiements");
        request.setAttribute("membre", membre);
        request.setAttribute("transactions", transactionService.findByMembre(membreId));
        ServletUtil.forward(request, response, "membre/transactions.jsp");
    }
}
