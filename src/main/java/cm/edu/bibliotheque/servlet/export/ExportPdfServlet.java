package cm.edu.bibliotheque.servlet.export;

import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.service.EmpruntService;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.service.PdfService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/export/pdf/*")
public class ExportPdfServlet extends HttpServlet {
    private final PdfService pdfService = new PdfService();
    private final MembreService membreService = new MembreService();
    private final EmpruntService empruntService = new EmpruntService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String type = request.getPathInfo() == null ? "rapport" : request.getPathInfo().substring(1);
        switch (type) {
            case "membre" -> exportCarteMembre(request, response);
            case "emprunt" -> exportRecuEmprunt(request, response);
            case "rapport" -> exportRapport(request, response);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Type PDF inconnu.");
        }
    }

    private void exportCarteMembre(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        Membre membre = id == null ? null : membreService.findById(id);
        if (membre == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        writePdf(response, "carte-membre-" + membre.getCarteNumero() + ".pdf", pdfService.genererCarteMembre(membre));
    }

    private void exportRecuEmprunt(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = parseLong(request.getParameter("id"));
        Emprunt emprunt = id == null ? null : empruntService.findByIdWithDetails(id);
        if (emprunt == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        writePdf(response, "recu-emprunt-" + emprunt.getId() + ".pdf", pdfService.genererRecuEmprunt(emprunt));
    }

    private void exportRapport(HttpServletRequest request, HttpServletResponse response) throws IOException {
        LocalDate today = LocalDate.now();
        int mois = parseInt(request.getParameter("mois"), today.getMonthValue());
        int annee = parseInt(request.getParameter("annee"), today.getYear());
        writePdf(response, "rapport-" + mois + "-" + annee + ".pdf", pdfService.genererRapportMensuel(mois, annee));
    }

    private void writePdf(HttpServletResponse response, String filename, byte[] bytes) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
    }

    private Long parseLong(String value) {
        try {
            return value == null || value.isBlank() ? null : Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
