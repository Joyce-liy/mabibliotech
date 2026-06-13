package cm.edu.bibliotheque.dto;

public class StatMensuelleDTO {
    private final int annee;
    private final int mois;
    private final String nomMois;
    private final long nbEmprunts;

    public StatMensuelleDTO(int annee, int mois, String nomMois, long nbEmprunts) {
        this.annee = annee;
        this.mois = mois;
        this.nomMois = nomMois;
        this.nbEmprunts = nbEmprunts;
    }

    public int getAnnee() {
        return annee;
    }

    public int getMois() {
        return mois;
    }

    public String getNomMois() {
        return nomMois;
    }

    public long getNbEmprunts() {
        return nbEmprunts;
    }
}
