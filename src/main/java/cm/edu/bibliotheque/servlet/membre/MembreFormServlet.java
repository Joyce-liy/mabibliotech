package cm.edu.bibliotheque.servlet.membre;

import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.enums.TypeMembre;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.util.DateUtil;
import cm.edu.bibliotheque.util.ServletUtil;
import cm.edu.bibliotheque.util.UploadUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Path;

@WebServlet({"/membres/nouveau", "/membres/modifier/*", "/membres/sauvegarder"})
@MultipartConfig
public class MembreFormServlet extends HttpServlet {
    private final MembreService membreService = new MembreService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = ServletUtil.getPathId(request);
        Membre membre = id == null ? new Membre() : membreService.findById(id);
        if (id == null) {
            membre.setCarteNumero(membreService.generateCarteNumero());
        }
        request.setAttribute("pageTitle", id == null ? "Nouveau membre" : "Modifier membre");
        request.setAttribute("membre", membre);
        request.setAttribute("types", TypeMembre.values());
        ServletUtil.forward(request, response, "membre/form.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Membre membre = readMembre(request);
        try {
            String filename = UploadUtil.saveImage(request.getPart("photo"), uploadDirectory());
            if (filename != null) {
                membre.setPhoto(filename);
            }
            membreService.save(membre);
            ServletUtil.flash(request, "success", "Membre enregistre avec succes.");
            ServletUtil.redirect(request, response, "/membres");
        } catch (BusinessException | ServletException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("membre", membre);
            request.setAttribute("types", TypeMembre.values());
            ServletUtil.forward(request, response, "membre/form.jsp");
        }
    }

    private Membre readMembre(HttpServletRequest request) {
        Long id = ServletUtil.getLongParameter(request, "id");
        Membre membre = id == null ? new Membre() : membreService.findById(id);
        membre.setCarteNumero(request.getParameter("carteNumero"));
        membre.setNom(request.getParameter("nom"));
        membre.setPrenom(request.getParameter("prenom"));
        membre.setTypeMembre(TypeMembre.valueOf(request.getParameter("typeMembre")));
        membre.setTelephone(blankToNull(request.getParameter("telephone")));
        membre.setEmail(blankToNull(request.getParameter("email")));
        membre.setDateExpirationCarte(DateUtil.parseIso(request.getParameter("dateExpirationCarte")));
        membre.setActif(request.getParameter("actif") == null || "true".equals(request.getParameter("actif")));
        return membre;
    }

    private Path uploadDirectory() {
        String realPath = getServletContext().getRealPath("/uploads/photos");
        return realPath == null ? Path.of(System.getProperty("java.io.tmpdir"), "bibliotheque-uploads") : Path.of(realPath);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
