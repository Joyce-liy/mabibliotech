package cm.edu.bibliotheque.service;

import java.math.BigDecimal;
import java.util.List;

import cm.edu.bibliotheque.dao.TransactionPaiementDAO;
import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.entity.Penalite;
import cm.edu.bibliotheque.entity.TransactionPaiement;

public class TransactionService {
    private final TransactionPaiementDAO transactionDAO = new TransactionPaiementDAO();

    public TransactionPaiement enregistrer(Membre membre, Penalite penalite, BigDecimal montant, String moyen, String reference) {
        TransactionPaiement t = new TransactionPaiement();
        t.setMembre(membre);
        t.setPenalite(penalite);
        t.setMontant(montant);
        t.setMoyen(moyen);
        t.setReference(reference);
        return transactionDAO.save(t);
    }

    public List<TransactionPaiement> findByMembre(Long membreId) {
        return transactionDAO.findByMembre(membreId);
    }

    public List<TransactionPaiement> findAll(int page, int pageSize) {
        return transactionDAO.findAllWithDetails(page, pageSize);
    }

    public long count() {
        return transactionDAO.count();
    }
}
