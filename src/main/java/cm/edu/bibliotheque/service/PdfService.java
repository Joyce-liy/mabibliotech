package cm.edu.bibliotheque.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;

import cm.edu.bibliotheque.dto.AnalyseCategorieDTO;
import cm.edu.bibliotheque.dto.StatMensuelleDTO;
import cm.edu.bibliotheque.dto.StatOuvrageDTO;
import cm.edu.bibliotheque.entity.Emprunt;
import cm.edu.bibliotheque.entity.Membre;

public class PdfService {
    private final StatistiqueService statistiqueService = new StatistiqueService();

    public byte[] genererCarteMembre(Membre membre) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf, PageSize.A6);

        // Layout with photo on the left
        Table header = new Table(UnitValue.createPercentArray(new float[]{1, 2})).useAllAvailableWidth();
        // photo cell
        Cell photoCell = new Cell().setBorder(null);
        try {
            if (membre.getPhoto() != null && !membre.getPhoto().isBlank()) {
                String photoPath = System.getProperty("catalina.base", "") ; // placeholder
                // Try to load from classpath uploads relative path
                java.io.InputStream is = PdfService.class.getClassLoader().getResourceAsStream("uploads/photos/" + membre.getPhoto());
                if (is == null) {
                    // try file system relative to project
                    java.nio.file.Path p = java.nio.file.Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "uploads", "photos", membre.getPhoto());
                    if (java.nio.file.Files.exists(p)) {
                        is = java.nio.file.Files.newInputStream(p);
                    }
                }
                if (is != null) {
                    byte[] bytes = is.readAllBytes();
                    com.itextpdf.io.image.ImageData img = com.itextpdf.io.image.ImageDataFactory.create(bytes);
                    com.itextpdf.layout.element.Image image = new com.itextpdf.layout.element.Image(img).scaleToFit(80, 80);
                    photoCell.add(image);
                }
            }
        } catch (Exception ex) {
            // ignore, no photo
        }
        header.addCell(photoCell);

        Cell info = new Cell().setBorder(null);
        info.add(new Paragraph("BIBLIOTHEQUE UNIVERSITAIRE").setBold().setFontSize(13));
        info.add(new Paragraph("Carte de membre").setFontSize(10));
        info.add(new Paragraph(membre.getCarteNumero()).setBold().setFontSize(18));
        info.add(new Paragraph(membre.getNom() + " " + membre.getPrenom()));
        info.add(new Paragraph("Type: " + membre.getTypeMembre()));
        info.add(new Paragraph("Expiration: " + membre.getDateExpirationCarte()));
        info.add(new Paragraph("Edition: " + LocalDate.now()).setFontSize(8));
        header.addCell(info);

        document.add(header);

        document.close();
        return out.toByteArray();
    }

    public byte[] genererRecuEmprunt(Emprunt emprunt) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf, PageSize.A4);

        document.add(new Paragraph("RECU D'EMPRUNT").setBold().setFontSize(16));
        document.add(new Paragraph("Date d'edition: " + LocalDate.now()));
        document.add(new Paragraph("Membre: " + emprunt.getMembre().getCarteNumero() + " - "
                + emprunt.getMembre().getNom() + " " + emprunt.getMembre().getPrenom()));
        document.add(new Paragraph("Type: " + emprunt.getMembre().getTypeMembre()));
        document.add(new Paragraph("Ouvrage: " + emprunt.getOuvrage().getTitre()));
        document.add(new Paragraph("Auteur: " + emprunt.getOuvrage().getAuteur()));
        document.add(new Paragraph("ISBN: " + value(emprunt.getOuvrage().getIsbn())));
        document.add(new Paragraph("Localisation: " + value(emprunt.getOuvrage().getLocalisation())));
        document.add(new Paragraph("Date emprunt: " + emprunt.getDateEmprunt()));
        document.add(new Paragraph("Date retour prevue: " + emprunt.getDateRetourPrevue()));
        document.add(new Paragraph("Tout retard sera facture a 50 FCFA/jour.").setBold());
        document.add(new Paragraph("Signature du bibliothecaire: ____________________"));
        document.add(new Paragraph("Numero d'emprunt: " + emprunt.getId()).setFontSize(9));

        document.close();
        return out.toByteArray();
    }

    public byte[] genererRapportMensuel(int mois, int annee) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfDocument pdf = new PdfDocument(new PdfWriter(out));
        Document document = new Document(pdf, PageSize.A4);

        String moisLabel = Month.of(mois).getDisplayName(TextStyle.FULL, Locale.FRENCH);
        document.add(new Paragraph("RAPPORT MENSUEL").setBold().setFontSize(18));
        document.add(new Paragraph("Periode: " + moisLabel + " " + annee));
        document.add(new Paragraph("Date d'edition: " + LocalDate.now()));

        document.add(new Paragraph("Top 10 des ouvrages les plus empruntes").setBold());
        Table topTable = new Table(UnitValue.createPercentArray(new float[]{4, 3, 2, 1})).useAllAvailableWidth();
        addHeader(topTable, "Titre", "Auteur", "Categorie", "Nb");
        for (StatOuvrageDTO stat : statistiqueService.getTopOuvragesEmpruntes(10)) {
            topTable.addCell(value(stat.getTitre()));
            topTable.addCell(value(stat.getAuteur()));
            topTable.addCell(value(stat.getCategorie()));
            topTable.addCell(String.valueOf(stat.getNbEmprunts()));
        }
        document.add(topTable);

        document.add(new Paragraph("Taux de retard par categorie").setBold());
        Table retardTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2})).useAllAvailableWidth();
        addHeader(retardTable, "Categorie", "Total", "Retards", "Taux %");
        for (AnalyseCategorieDTO stat : statistiqueService.getTauxRetardParCategorie()) {
            retardTable.addCell(value(stat.getCategorie()));
            retardTable.addCell(String.valueOf(stat.getTotalEmprunts()));
            retardTable.addCell(String.valueOf(stat.getTotalRetards()));
            // Correction de cohérence : utiliser getTaux() à la place de getTauxRetardPct()
            retardTable.addCell(String.format(Locale.US, "%.1f %%", stat.getTaux())); 
        }
        document.add(retardTable);

        document.add(new Paragraph("Evolution mensuelle").setBold());
        Table moisTable = new Table(UnitValue.createPercentArray(new float[]{1, 3, 2})).useAllAvailableWidth();
        addHeader(moisTable, "Mois", "Nom", "Emprunts");
        for (StatMensuelleDTO stat : statistiqueService.getEvolutionMensuelle(annee)) {
            moisTable.addCell(String.valueOf(stat.getMois()));
            moisTable.addCell(value(stat.getNomMois()));
            moisTable.addCell(String.valueOf(stat.getNbEmprunts()));
        }
        document.add(moisTable);

        document.close();
        return out.toByteArray();
    }

    private void addHeader(Table table, String... labels) {
        for (String label : labels) {
            table.addHeaderCell(new Cell().add(new Paragraph(label).setBold()));
        }
    }

 // Remplace ton ancienne méthode utilitaire par celle-ci sécurisée :
    private String value(String value) {
        if (value == null || "null".equalsIgnoreCase(value)) {
            return ""; // Ou "Inconnu" selon ton choix d'affichage
        }
        return value;
    }
}
