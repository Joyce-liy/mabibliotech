package cm.edu.bibliotheque.service;

import cm.edu.bibliotheque.dao.OuvrageDAO;
import cm.edu.bibliotheque.entity.Ouvrage;
import cm.edu.bibliotheque.exception.BusinessException;
import java.util.List;

public class OuvrageService {
    private final OuvrageDAO ouvrageDAO = new OuvrageDAO();

    public Ouvrage findById(Long id) {
        return ouvrageDAO.findById(id);
    }

    public List<Ouvrage> findAll(int page, int pageSize) {
        return ouvrageDAO.findWithPagination(page, pageSize);
    }

    public List<Ouvrage> search(String query, String categorie) {
        if (query != null && !query.isBlank()) {
            return ouvrageDAO.searchFullText(query.trim());
        }
        if (categorie != null && !categorie.isBlank()) {
            return ouvrageDAO.findByCategorie(categorie);
        }
        return ouvrageDAO.findAll();
    }

    public List<Ouvrage> findDisponibles() {
        return ouvrageDAO.findDisponibles();
    }

    public List<String> findCategories() {
        return ouvrageDAO.findCategories();
    }

    public long count() {
        return ouvrageDAO.count();
    }

    public Ouvrage save(Ouvrage ouvrage) throws BusinessException {
        validate(ouvrage);
        return ouvrageDAO.save(ouvrage);
    }

    public void supprimer(Long id) throws BusinessException {
        if (ouvrageDAO.hasEmpruntEnCours(id)) {
            throw new BusinessException("Impossible de supprimer un ouvrage avec des emprunts en cours.");
        }
        ouvrageDAO.delete(id);
    }

    private void validate(Ouvrage ouvrage) throws BusinessException {
        if (ouvrage.getTitre() == null || ouvrage.getTitre().isBlank()) {
            throw new BusinessException("Le titre est obligatoire.");
        }
        if (ouvrage.getAuteur() == null || ouvrage.getAuteur().isBlank()) {
            throw new BusinessException("L'auteur est obligatoire.");
        }
        if (ouvrage.getExemplairesTotal() == null || ouvrage.getExemplairesTotal() < 1) {
            ouvrage.setExemplairesTotal(1);
        }
        if (ouvrage.getExemplairesDispo() == null) {
            ouvrage.setExemplairesDispo(ouvrage.getExemplairesTotal());
        }
        if (ouvrage.getExemplairesDispo() < 0) {
            throw new BusinessException("Le nombre d'exemplaires disponibles ne peut pas etre negatif.");
        }
        if (ouvrage.getExemplairesDispo() > ouvrage.getExemplairesTotal()) {
            throw new BusinessException("Les exemplaires disponibles doivent etre inferieurs ou egaux au total.");
        }
    }
}
