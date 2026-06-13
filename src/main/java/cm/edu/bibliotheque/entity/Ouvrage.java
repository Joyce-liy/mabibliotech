package cm.edu.bibliotheque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "ouvrage")
public class Ouvrage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(nullable = false, length = 150)
    private String auteur;

    @Column(length = 100)
    private String editeur;

    @Column(name = "annee_edition")
    private Integer anneeEdition;

    @Column(length = 80)
    private String categorie;

    @Column(name = "exemplaires_total")
    private Integer exemplairesTotal = 1;

    @Column(name = "exemplaires_dispo")
    private Integer exemplairesDispo = 1;

    @Column(length = 50)
    private String localisation;

    @Column(name = "photo_couverture", length = 255)
    private String photoCouverture;

    @Column(name = "date_ajout")
    private LocalDateTime dateAjout = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getEditeur() {
        return editeur;
    }

    public void setEditeur(String editeur) {
        this.editeur = editeur;
    }

    public Integer getAnneeEdition() {
        return anneeEdition;
    }

    public void setAnneeEdition(Integer anneeEdition) {
        this.anneeEdition = anneeEdition;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public Integer getExemplairesTotal() {
        return exemplairesTotal;
    }

    public void setExemplairesTotal(Integer exemplairesTotal) {
        this.exemplairesTotal = exemplairesTotal;
    }

    public Integer getExemplairesDispo() {
        return exemplairesDispo;
    }

    public void setExemplairesDispo(Integer exemplairesDispo) {
        this.exemplairesDispo = exemplairesDispo;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getPhotoCouverture() {
        return photoCouverture;
    }

    public void setPhotoCouverture(String photoCouverture) {
        this.photoCouverture = photoCouverture;
    }

    public LocalDateTime getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(LocalDateTime dateAjout) {
        this.dateAjout = dateAjout;
    }
}
