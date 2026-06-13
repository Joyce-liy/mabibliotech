USE bibliotheque_universitaire;

-- L'application cree aussi ce compte au demarrage si la table utilisateur est vide.
-- Mot de passe: admin123
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role)
SELECT 'Admin', 'Systeme', 'admin@bibliotheque.univ.cm',
       '$2a$12$LDzcWbeGYrgPFhNlQ1VGWeu1XhTv29E62A8pC6/YiBEZKHfd8cw5m',
       'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM utilisateur WHERE email = 'admin@bibliotheque.univ.cm'
);

INSERT INTO ouvrage (isbn, titre, auteur, editeur, annee_edition, categorie, exemplaires_total, exemplaires_dispo, localisation)
VALUES
('9780134685991', 'Effective Java', 'Joshua Bloch', 'Addison-Wesley', 2018, 'Informatique', 3, 3, 'A1-R1'),
('9782212674745', 'UML 2 par la pratique', 'Pascal Roques', 'Eyrolles', 2018, 'Informatique', 2, 2, 'A1-R2'),
('9782247188712', 'Introduction au droit', 'Collectif', 'Dalloz', 2021, 'Droit', 4, 4, 'B2-R1');

INSERT INTO membre (carte_numero, nom, prenom, type_membre, telephone, email, date_expiration_carte)
VALUES
('BU-2026-0001', 'Ngono', 'Alice', 'ETUDIANT', '+237690000001', 'alice.ngono@example.com', DATE_ADD(CURRENT_DATE, INTERVAL 1 YEAR)),
('BU-2026-0002', 'Fouda', 'Marc', 'ENSEIGNANT', '+237690000002', 'marc.fouda@example.com', DATE_ADD(CURRENT_DATE, INTERVAL 1 YEAR));
