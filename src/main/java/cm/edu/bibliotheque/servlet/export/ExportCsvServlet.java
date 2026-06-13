package cm.edu.bibliotheque.servlet.export;

import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.entity.Ouvrage;
import cm.edu.bibliotheque.service.EmpruntService;
import cm.edu.bibliotheque.service.MembreService;
import cm.edu.bibliotheque.service.OuvrageService;
import com.opencsv.CSVWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/export/csv/*")
public class ExportCsvServlet extends HttpServlet {
    private final OuvrageService ouvrageService = new OuvrageService();
    private final MembreService membreService = new MembreService();
    private final EmpruntService empruntService = new EmpruntService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String entity = request.getPathInfo() == null ? "" : request.getPathInfo().substring(1);
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + entity + ".csv\"");

        PrintWriter writer = response.getWriter();
        writer.write('\uFEFF');
        CSVWriter csv = new CSVWriter(writer, ';', CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);

        switch (entity) {
            case "ouvrages" -> writeOuvrages(csv);
            case "membres" -> writeMembres(csv);
            case "emprunts" -> writeEmprunts(csv);
            default -> response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Entite exportable inconnue.");
        }
        csv.flush();
    }

    private void writeOuvrages(CSVWriter csv) {
        csv.writeNext(new String[]{"ISBN", "Titre", "Auteur", "Categorie", "Total", "Disponibles"});
        for (Ouvrage ouvrage : ouvrageService.search(null, null)) {
            csv.writeNext(new String[]{
                    value(ouvrage.getIsbn()),
                    value(ouvrage.getTitre()),
                    value(ouvrage.getAuteur()),
                    value(ouvrage.getCategorie()),
                    String.valueOf(ouvrage.getExemplairesTotal()),
                    String.valueOf(ouvrage.getExemplairesDispo())
            });
        }
    }

    private void writeMembres(CSVWriter csv) {
        csv.writeNext(new String[]{"Carte", "Nom", "Prenom", "Type", "Telephone", "Email", "Actif"});
        for (Membre membre : membreService.search(null, null)) {
            csv.writeNext(new String[]{
                    value(membre.getCarteNumero()),
                    value(membre.getNom()),
                    value(membre.getPrenom()),
                    String.valueOf(membre.getTypeMembre()),
                    value(membre.getTelephone()),
                    value(membre.getEmail()),
                    String.valueOf(membre.getActif())
            });
        }
    }

    private void writeEmprunts(CSVWriter csv) {
        csv.writeNext(new String[]{"Membre", "Ouvrage", "Date emprunt", "Retour prevu", "Retour reel", "Statut"});
        for (Emprunt emprunt : empruntService.findAllWithDetails(1, 10000)) {
            csv.writeNext(new String[]{
                    emprunt.getMembre().getCarteNumero(),
                    emprunt.getOuvrage().getTitre(),
                    String.valueOf(emprunt.getDateEmprunt()),
                    String.valueOf(emprunt.getDateRetourPrevue()),
                    String.valueOf(emprunt.getDateRetourReelle()),
                    String.valueOf(emprunt.getStatut())
            });
        }
    }

    private String value(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
