package cm.edu.bibliotheque.dto;

public class StatCategorieDTO {
    private final String categorie;
    private final long totalEmprunts;
    private final long nbRetards;
    private final double tauxRetardPct;

    public StatCategorieDTO(String categorie, long totalEmprunts, long nbRetards, double tauxRetardPct) {
        this.categorie = categorie;
        this.totalEmprunts = totalEmprunts;
        this.nbRetards = nbRetards;
        this.tauxRetardPct = tauxRetardPct;
    }

    public String getCategorie() {
        return categorie;
    }

    public long getTotalEmprunts() {
        return totalEmprunts;
    }

    public long getNbRetards() {
        return nbRetards;
    }

    public double getTauxRetardPct() {
        return tauxRetardPct;
    }
}
