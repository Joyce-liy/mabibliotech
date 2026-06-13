package cm.edu.bibliotheque.dto;

public class StatOuvrageDTO {
    private final String titre;
    private final String auteur;
    private final String categorie;
    private final long nbEmprunts;

    public StatOuvrageDTO(String titre, String auteur, String categorie, long nbEmprunts) {
        this.titre = titre;
        this.auteur = auteur;
        this.categorie = categorie;
        this.nbEmprunts = nbEmprunts;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getCategorie() {
        return categorie;
    }

    public long getNbEmprunts() {
        return nbEmprunts;
    }
}
