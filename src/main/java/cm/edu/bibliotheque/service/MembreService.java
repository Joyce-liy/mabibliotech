package cm.edu.bibliotheque.service;

import cm.edu.bibliotheque.dao.MembreDAO;
import cm.edu.bibliotheque.entity.Membre;
import cm.edu.bibliotheque.enums.TypeMembre;
import cm.edu.bibliotheque.exception.BusinessException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class MembreService {
    private final MembreDAO membreDAO = new MembreDAO();
    private final BlocageService blocageService = new BlocageService();

    public Membre findById(Long id) {
        return membreDAO.findById(id);
    }

    public List<Membre> findAll(int page, int pageSize) {
        return membreDAO.findWithPagination(page, pageSize);
    }

    public List<Membre> search(String query, TypeMembre type) {
        if (query != null && !query.isBlank()) {
            return membreDAO.findByNomOrPrenom(query.trim());
        }
        if (type != null) {
            return membreDAO.findByTypeMembre(type);
        }
        return membreDAO.findAll();
    }

    public List<Membre> findActifs() {
        return membreDAO.findActifs();
    }

    public List<Membre> findBloques() {
        return membreDAO.findBloques();
    }

    public long count() {
        return membreDAO.count();
    }

    public long countActifs() {
        return membreDAO.countActifs();
    }

    public Membre save(Membre membre) throws BusinessException {
        validate(membre);
        if (membre.getCarteNumero() == null || membre.getCarteNumero().isBlank()) {
            membre.setCarteNumero(generateCarteNumero());
        }
        if (membre.getDateExpirationCarte() == null) {
            membre.setDateExpirationCarte(LocalDate.now().plusYears(1));
        }
        return membreDAO.save(membre);
    }

    public void supprimer(Long id) throws BusinessException {
        if (membreDAO.hasEmpruntEnCours(id)) {
            throw new BusinessException("Impossible de supprimer un membre avec des emprunts en cours.");
        }
        membreDAO.desactiver(id);
    }

    public int evaluerBlocagesAutomatiques() {
        return blocageService.evaluerTous();
    }

    public void debloquerManuellement(Long id) throws BusinessException {
        blocageService.debloquerManuellement(id);
    }

    public String generateCarteNumero() {
        return "BU-" + LocalDate.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private void validate(Membre membre) throws BusinessException {
        if (membre.getNom() == null || membre.getNom().isBlank()) {
            throw new BusinessException("Le nom est obligatoire.");
        }
        if (membre.getPrenom() == null || membre.getPrenom().isBlank()) {
            throw new BusinessException("Le prenom est obligatoire.");
        }
        if (membre.getTypeMembre() == null) {
            throw new BusinessException("Le type de membre est obligatoire.");
        }
    }
}
