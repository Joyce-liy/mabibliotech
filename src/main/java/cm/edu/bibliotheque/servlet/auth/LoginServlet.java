package cm.edu.bibliotheque.servlet.auth;

import cm.edu.bibliotheque.entity.Utilisateur;
import cm.edu.bibliotheque.service.AuthService;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginServlet.class);
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession(false) != null && request.getSession(false).getAttribute("utilisateur") != null) {
            ServletUtil.redirect(request, response, "/dashboard");
            return;
        }
        ServletUtil.forward(request, response, "auth/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("motDePasse");
        Utilisateur utilisateur = authService.authenticate(email, password);

        if (utilisateur == null) {
            request.setAttribute("error", "Email ou mot de passe incorrect.");
            ServletUtil.forward(request, response, "auth/login.jsp");
            return;
        }

        HttpSession oldSession = request.getSession(false);
        if (oldSession != null) {
            oldSession.invalidate();
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("utilisateur", utilisateur);
        session.setMaxInactiveInterval(30 * 60);
        LOGGER.info("Connexion utilisateur {}", utilisateur.getEmail());
        ServletUtil.redirect(request, response, "/dashboard");
    }
}
