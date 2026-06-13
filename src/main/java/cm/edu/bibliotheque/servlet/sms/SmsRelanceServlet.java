package cm.edu.bibliotheque.servlet.sms;

import java.io.IOException;
import java.util.List;

import cm.edu.bibliotheque.dao.EmpruntDAO;
import cm.edu.bibliotheque.dao.PenaliteDAO;
import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.entity.Penalite;
import cm.edu.bibliotheque.service.SmsService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/sms/relances")
public class SmsRelanceServlet extends HttpServlet {
    private final SmsService smsService = new SmsService();
    private final EmpruntDAO empruntDAO = new EmpruntDAO();
    private final PenaliteDAO penaliteDAO = new PenaliteDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Emprunt> retards = empruntDAO.findEnRetard();
        int sent = 0;
        for (Emprunt e : retards) {
            Penalite p = penaliteDAO.findByEmprunt(e.getId());
            smsService.envoyerRelanceRetard(e, p);
            sent++;
        }
        ServletUtil.flash(request, "success", "Relances envoyées: " + sent);
        ServletUtil.redirect(request, response, "/dashboard");
    }
}
