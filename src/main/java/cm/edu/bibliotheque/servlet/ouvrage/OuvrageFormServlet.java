package cm.edu.bibliotheque.servlet.ouvrage;

import cm.edu.bibliotheque.entity.Ouvrage;
import cm.edu.bibliotheque.exception.BusinessException;
import cm.edu.bibliotheque.service.OuvrageService;
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

@WebServlet({"/ouvrages/nouveau", "/ouvrages/modifier/*", "/ouvrages/sauvegarder"})
@MultipartConfig
public class OuvrageFormServlet extends HttpServlet {
    private final OuvrageService ouvrageService = new OuvrageService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = ServletUtil.getPathId(request);
        Ouvrage ouvrage = id == null ? new Ouvrage() : ouvrageService.findById(id);
        request.setAttribute("pageTitle", id == null ? "Nouvel ouvrage" : "Modifier ouvrage");
        request.setAttribute("ouvrage", ouvrage);
        ServletUtil.forward(request, response, "ouvrage/form.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Ouvrage ouvrage = readOuvrage(request);
        try {
            String filename = UploadUtil.saveImage(request.getPart("photoCouverture"), uploadDirectory());
            if (filename != null) {
                ouvrage.setPhotoCouverture(filename);
            }
            ouvrageService.save(ouvrage);
            ServletUtil.flash(request, "success", "Ouvrage enregistre avec succes.");
            ServletUtil.redirect(request, response, "/ouvrages");
        } catch (BusinessException | ServletException ex) {
            request.setAttribute("error", ex.getMessage());
            request.setAttribute("ouvrage", ouvrage);
            ServletUtil.forward(request, response, "ouvrage/form.jsp");
        }
    }

    private Ouvrage readOuvrage(HttpServletRequest request) {
        Long id = ServletUtil.getLongParameter(request, "id");
        Ouvrage ouvrage = id == null ? new Ouvrage() : ouvrageService.findById(id);
        ouvrage.setIsbn(blankToNull(request.getParameter("isbn")));
        ouvrage.setTitre(request.getParameter("titre"));
        ouvrage.setAuteur(request.getParameter("auteur"));
        ouvrage.setEditeur(blankToNull(request.getParameter("editeur")));
        ouvrage.setAnneeEdition(ServletUtil.getIntegerParameter(request, "anneeEdition"));
        ouvrage.setCategorie(blankToNull(request.getParameter("categorie")));
        ouvrage.setExemplairesTotal(ServletUtil.getIntegerParameter(request, "exemplairesTotal"));
        ouvrage.setExemplairesDispo(ServletUtil.getIntegerParameter(request, "exemplairesDispo"));
        ouvrage.setLocalisation(blankToNull(request.getParameter("localisation")));
        return ouvrage;
    }

    private Path uploadDirectory() {
        String realPath = getServletContext().getRealPath("/uploads/photos");
        return realPath == null ? Path.of(System.getProperty("java.io.tmpdir"), "bibliotheque-uploads") : Path.of(realPath);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
