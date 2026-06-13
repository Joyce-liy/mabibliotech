package cm.edu.bibliotheque.service;

import cm.edu.bibliotheque.dao.EmpruntDAO;
import cm.edu.bibliotheque.dao.MembreDAO;
import cm.edu.bibliotheque.dao.OuvrageDAO;
import cm.edu.bibliotheque.dao.PenaliteDAO;
import cm.edu.bibliotheque.dto.DashboardStatsDTO;
import cm.edu.bibliotheque.dto.AnalyseCategorieDTO; // CORRECTION : Importation du bon DTO
import cm.edu.bibliotheque.dto.StatMensuelleDTO;
import cm.edu.bibliotheque.dto.StatOuvrageDTO;
import cm.edu.bibliotheque.enums.StatutEmprunt;
import java.math.BigDecimal;
import java.util.List;

public class StatistiqueService {
    private final OuvrageDAO ouvrageDAO = new OuvrageDAO();
    private final MembreDAO membreDAO = new MembreDAO();
    private final EmpruntDAO empruntDAO = new EmpruntDAO();
    private final PenaliteDAO penaliteDAO = new PenaliteDAO();

    public List<StatOuvrageDTO> getTopOuvragesEmpruntes(int limit) {
        return ouvrageDAO.findTopEmpruntes(limit).stream()
                .map(row -> new StatOuvrageDTO(
                        (String) row[0],
                        (String) row[1],
                        (String) row[2],
                        asLong(row[3])))
                .toList();
    }

    public List<AnalyseCategorieDTO> getTauxRetardParCategorie() {
        return empruntDAO.tauxRetardParCategorie().stream()
                .map(row -> new AnalyseCategorieDTO(
                        String.valueOf(row[0]),
                        asLong(row[1]),
                        asLong(row[2]),
                        asDouble(row[3])))
                .toList();
    }

    public List<StatMensuelleDTO> getEvolutionMensuelle(int annee) {
        return empruntDAO.countByMois(annee).stream()
                .map(row -> new StatMensuelleDTO(
                        asInt(row[0]),
                        asInt(row[1]),
                        String.valueOf(row[2]),
                        asLong(row[3])))
                .toList();
    }

    public DashboardStatsDTO getDashboardStats() {
        BigDecimal montant = penaliteDAO.sumImpayees();
        return new DashboardStatsDTO(
                ouvrageDAO.count(),
                membreDAO.countActifs(),
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