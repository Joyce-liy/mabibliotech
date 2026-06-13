CREATE DATABASE IF NOT EXISTS bibliotheque_universitaire
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE bibliotheque_universitaire;

CREATE TABLE IF NOT EXISTS utilisateur (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom           VARCHAR(100) NOT NULL,
    prenom        VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe  VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN', 'BIBLIOTHECAIRE') DEFAULT 'BIBLIOTHECAIRE',
    actif         BOOLEAN DEFAULT TRUE,
    date_creation DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS ouvrage (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn                VARCHAR(20) UNIQUE,
    titre               VARCHAR(200) NOT NULL,
    auteur              VARCHAR(150) NOT NULL,
    editeur             VARCHAR(100),
    annee_edition       INT,
    categorie           VARCHAR(80),
    exemplaires_total   INT DEFAULT 1,
    exemplaires_dispo   INT DEFAULT 1,
    localisation        VARCHAR(50),
    photo_couverture    VARCHAR(255),
    date_ajout          DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_ouvrage_exemplaires CHECK (exemplaires_dispo >= 0 AND exemplaires_dispo <= exemplaires_total)
);

CREATE TABLE IF NOT EXISTS membre (
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    carte_numero          VARCHAR(20) NOT NULL UNIQUE,
    nom                   VARCHAR(100) NOT NULL,
    prenom                VARCHAR(100) NOT NULL,
    type_membre           ENUM('ETUDIANT','ENSEIGNANT','EXTERNE') NOT NULL,
    telephone             VARCHAR(20),
    email                 VARCHAR(150),
    date_expiration_carte DATE,
    actif                 BOOLEAN DEFAULT TRUE,
    bloque                BOOLEAN DEFAULT FALSE,
    photo                 VARCHAR(255),
    date_inscription      DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS emprunt (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    membre_id           BIGINT NOT NULL,
    ouvrage_id          BIGINT NOT NULL,
    date_emprunt        DATE NOT NULL DEFAULT (CURRENT_DATE),
    date_retour_prevue  DATE NOT NULL,
    date_retour_reelle  DATE,
    statut              ENUM('EN_COURS','RENDU','EN_RETARD') DEFAULT 'EN_COURS',
    biblioth_id         BIGINT,
    CONSTRAINT fk_emprunt_membre   FOREIGN KEY (membre_id)    REFERENCES membre(id),
    CONSTRAINT fk_emprunt_ouvrage  FOREIGN KEY (ouvrage_id)   REFERENCES ouvrage(id),
    CONSTRAINT fk_emprunt_bibliot  FOREIGN KEY (biblioth_id)  REFERENCES utilisateur(id)
);

CREATE TABLE IF NOT EXISTS penalite (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    emprunt_id    BIGINT NOT NULL UNIQUE,
    jours_retard  INT NOT NULL,
    montant_fcfa  DECIMAL(8,2),
    payee         BOOLEAN DEFAULT FALSE,
    date_calcul   DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_penalite_emprunt FOREIGN KEY (emprunt_id) REFERENCES emprunt(id)
);

CREATE TABLE IF NOT EXISTS transaction_paiement (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    membre_id      BIGINT NOT NULL,
    penalite_id    BIGINT,
    montant        DECIMAL(10,2) NOT NULL,
    date_paiement  DATETIME DEFAULT CURRENT_TIMESTAMP,
    moyen          VARCHAR(50),
    reference      VARCHAR(100),
    CONSTRAINT fk_transaction_membre FOREIGN KEY (membre_id) REFERENCES membre(id),
    CONSTRAINT fk_transaction_penalite FOREIGN KEY (penalite_id) REFERENCES penalite(id)
);

CREATE INDEX idx_emprunt_statut     ON emprunt(statut);
CREATE INDEX idx_emprunt_membre     ON emprunt(membre_id);
CREATE INDEX idx_emprunt_date       ON emprunt(date_emprunt);
CREATE INDEX idx_ouvrage_categorie  ON ouvrage(categorie);
CREATE INDEX idx_membre_type        ON membre(type_membre);
CREATE INDEX idx_membre_bloque      ON membre(bloque);
CREATE INDEX idx_transaction_membre ON transaction_paiement(membre_id);
