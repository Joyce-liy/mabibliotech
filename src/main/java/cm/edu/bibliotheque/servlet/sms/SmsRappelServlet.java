package cm.edu.bibliotheque.servlet.sms;

import java.io.IOException;

import cm.edu.bibliotheque.service.SmsService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/sms/rappels")
public class SmsRappelServlet extends HttpServlet {
    private final SmsService smsService = new SmsService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int sent = smsService.envoyerTousLesRappelsCount();
        ServletUtil.flash(request, "success", "Rappels SMS J-2 déclenchés. Nombre de rappels: " + sent);
        ServletUtil.redirect(request, response, "/dashboard");
    }
}
