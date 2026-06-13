package cm.edu.bibliotheque.dto;

public class AnalyseCategorieDTO {
    private final String categorie;
    private final long totalEmprunts;
    private final long totalRetards;
    private final double taux;

    public AnalyseCategorieDTO(String categorie, long totalEmprunts, long totalRetards, double taux) {
        this.categorie = categorie;
        this.totalEmprunts = totalEmprunts;
        this.totalRetards = totalRetards;
        this.taux = taux;
    }

    public String getCategorie() {
        return categorie;
    }

    public long getTotalEmprunts() {
        return totalEmprunts;
    }

    public long getTotalRetards() {
        return totalRetards;
    }

    public double getTaux() {
        return taux;
    }

	public char[] getTauxRetardPct() {
		// TODO Auto-generated method stub
		return null;
	}
}