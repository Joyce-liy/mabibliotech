package cm.edu.bibliotheque.service;

import cm.edu.bibliotheque.dao.EmpruntDAO;
import cm.edu.bibliotheque.dao.OuvrageDAO;
import cm.edu.bibliotheque.dao.PenaliteDAO;
import cm.edu.bibliotheque.dto.AnalyseCategorieDTO;
import cm.edu.bibliotheque.dto.DashboardStatsDTO;
import cm.edu.bibliotheque.dto.StatMensuelleDTO;
import cm.edu.bibliotheque.dto.StatOuvrageDTO;
import cm.edu.bibliotheque.enums.StatutEmprunt;
import java.math.BigDecimal;
import java.util.List;

public class AnalyseService {
    private final OuvrageDAO ouvrageDAO = new OuvrageDAO();
    private final EmpruntDAO empruntDAO = new EmpruntDAO();
    private final PenaliteDAO penaliteDAO = new PenaliteDAO();

    // Récupère le Top des ouvrages pour la page d'analyse (ex: Top 10)
    public List<StatOuvrageDTO> getTopOuvragesPourAnalyse(int limit) {
        return ouvrageDAO.findTopEmpruntes(limit).stream()
                .map(row -> new StatOuvrageDTO(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        asLong(row[3])))
                .toList();
    }

    // Calcule le taux de retard par catégorie avec le nouveau DTO d'analyse
    public List<AnalyseCategorieDTO> getTauxRetardParCategorie() {
        return empruntDAO.tauxRetardParCategorie().stream()
                .map(row -> new AnalyseCategorieDTO(
                        String.valueOf(row[0]), // Catégorie (Etudiant, Enseignant...)
                        asLong(row[1]),        // Total des emprunts
                        asLong(row[2]),        // Total des retards
                        asDouble(row[3])))     // % Taux de retard
                .toList();
    }

    // Récupère l'évolution mensuelle pour le graphique Chart.js
    public List<StatMensuelleDTO> getEvolutionMensuelle(int annee) {
        return empruntDAO.countByMois(annee).stream()
                .map(row -> new StatMensuelleDTO(
                        asInt(row[0]),
                        asInt(row[1]),
                        String.valueOf(row[2]),
                        asLong(row[3])))
                .toList();
    }

    // Chiffres clés du bandeau supérieur (KPIs)
    public DashboardStatsDTO getStatsGlobalesAnalyse() {
        BigDecimal montant = penaliteDAO.sumImpayees();
        return new DashboardStatsDTO(
                ouvrageDAO.count(),
                0L, // Non nécessaire sur l'analyse pure
                empruntDAO.countByStatut(StatutEmprunt.EN_COURS),
                empruntDAO.countByStatut(StatutEmprunt.EN_RETARD),
                penaliteDAO.countImpayees(),
                montant
        );
    }

    private long asLong(Object value) {
        return value == null ? 0L : ((Number) value).longValue();
    }

    private int asInt(Object value) {
        return value == null ? 0 : ((Number) value).intValue();
    }

    private double asDouble(Object value) {
        return value == null ? 0.0d : ((Number) value).doubleValue();
    }
}