package cm.edu.bibliotheque.servlet.penalite;

import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.PenaliteService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/penalites/payer/*")
public class PenalitePaiementServlet extends HttpServlet {
    private final PenaliteService penaliteService = new PenaliteService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Long penaliteId = ServletUtil.getPathId(request);
            BigDecimal montant = parseMontant(request.getParameter("montant"));
            penaliteService.enregistrerPaiement(
                    penaliteId,
                    montant,
                    request.getParameter("moyen"),
                    request.getParameter("reference"));
            ServletUtil.flash(request, "success", "Paiement enregistre et transaction sauvegardee.");
        } catch (BusinessException | RuntimeException ex) {
            ServletUtil.flash(request, "danger", ex.getMessage());
        }
        ServletUtil.redirect(request, response, "/penalites");
    }

    private BigDecimal parseMontant(String value) throws BusinessException {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new BusinessException("Le montant du paiement est invalide.");
        }
    }
}
