package cm.edu.bibliotheque.servlet.membre;

import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.entity.Utilisateur;
import cm.edu.bibliotheque.enums.RoleUtilisateur;
import cm.edu.bibliotheque.enums.TypeMembre;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.util.PaginationUtil;
import cm.edu.bibliotheque.util.ServletUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/membres")
public class MembreListServlet extends HttpServlet {
    private final MembreService membreService = new MembreService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int page = PaginationUtil.page(request);
        int size = PaginationUtil.size(request);
        String query = request.getParameter("q");
        TypeMembre type = parseType(request.getParameter("type"));
        membreService.evaluerBlocagesAutomatiques();

        List<Membre> membres;
        long total;
        if ((query != null && !query.isBlank()) || type != null) {
            membres = membreService.search(query, type);
            total = membres.size();
        } else {
            membres = membreService.findAll(page, size);
            total = membreService.count();
        }

        Utilisateur utilisateur = (Utilisateur) request.getSession().getAttribute("utilisateur");
        request.setAttribute("pageTitle", "Membres");
        request.setAttribute("membres", membres);
        request.setAttribute("types", TypeMembre.values());
        request.setAttribute("q", query);
        request.setAttribute("type", type);
        request.setAttribute("currentPage", page);
        request.setAttribute("pageSize", size);
        request.setAttribute("totalItems", total);
        request.setAttribute("totalPages", PaginationUtil.totalPages(total, size));
        request.setAttribute("isAdmin", utilisateur != null && RoleUtilisateur.ADMIN.equals(utilisateur.getRole()));
        ServletUtil.forward(request, response, "membre/list.jsp");
    }

    private TypeMembre parseType(String value) {
        try {
            return value == null || value.isBlank() ? null : TypeMembre.valueOf(value);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
