package cm.edu.bibliotheque.dto;

import java.math.BigDecimal;

public class DashboardStatsDTO {
    private final long totalOuvrages;
    private final long membresActifs;
    private final long empruntsEnCours;
    private final long empruntsEnRetard;
    private final long penalitesImpayees;
    private final BigDecimal montantPenalitesImpayees;

    public DashboardStatsDTO(long totalOuvrages, long membresActifs, long empruntsEnCours,
                             long empruntsEnRetard, long penalitesImpayees,
                             BigDecimal montantPenalitesImpayees) {
        this.totalOuvrages = totalOuvrages;
        this.membresActifs = membresActifs;
        this.empruntsEnCours = empruntsEnCours;
        this.empruntsEnRetard = empruntsEnRetard;
        this.penalitesImpayees = penalitesImpayees;
        this.montantPenalitesImpayees = montantPenalitesImpayees;
    }

    public long getTotalOuvrages() {
        return totalOuvrages;
    }

    public long getMembresActifs() {
        return membresActifs;
    }

    public long getEmpruntsEnCours() {
        return empruntsEnCours;
    }

    public long getEmpruntsEnRetard() {
        return empruntsEnRetard;
    }

    public long getPenalitesImpayees() {
        return penalitesImpayees;
    }

    public BigDecimal getMontantPenalitesImpayees() {
        return montantPenalitesImpayees;
    }
}
