package cm.edu.bibliotheque.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "penalite")
public class Penalite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emprunt_id", nullable = false, unique = true)
    private Emprunt emprunt;

    @Column(name = "jours_retard", nullable = false)
    private Integer joursRetard;

    @Column(name = "montant_fcfa", precision = 8, scale = 2)
    private BigDecimal montantFcfa;

    private Boolean payee = false;

    @Column(name = "date_calcul")
    private LocalDate dateCalcul = LocalDate.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emprunt getEmprunt() {
        return emprunt;
    }

    public void setEmprunt(Emprunt emprunt) {
        this.emprunt = emprunt;
    }

    public Integer getJoursRetard() {
        return joursRetard;
    }

    public void setJoursRetard(Integer joursRetard) {
        this.joursRetard = joursRetard;
    }

    public BigDecimal getMontantFcfa() {
        return montantFcfa;
    }

    public void setMontantFcfa(BigDecimal montantFcfa) {
        this.montantFcfa = montantFcfa;
    }

    public Boolean getPayee() {
        return payee;
    }

    public void setPayee(Boolean payee) {
        this.payee = payee;
    }

    public LocalDate getDateCalcul() {
        return dateCalcul;
    }

    public void setDateCalcul(LocalDate dateCalcul) {
        this.dateCalcul = dateCalcul;
    }
}
