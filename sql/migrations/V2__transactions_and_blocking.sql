-- Migration: add transaction_paiement table and bloque column on membre
SET @has_bloque := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'membre'
    AND column_name = 'bloque'
);
SET @add_bloque := IF(@has_bloque = 0,
  'ALTER TABLE membre ADD COLUMN bloque BOOLEAN DEFAULT FALSE',
  'SELECT 1'
);
PREPARE add_bloque_stmt FROM @add_bloque;
EXECUTE add_bloque_stmt;
DEALLOCATE PREPARE add_bloque_stmt;

CREATE TABLE IF NOT EXISTS transaction_paiement (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  membre_id BIGINT NOT NULL,
  penalite_id BIGINT,
  montant DECIMAL(10,2) NOT NULL,
  date_paiement TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  moyen VARCHAR(50),
  reference VARCHAR(100),
  CONSTRAINT fk_transaction_membre FOREIGN KEY (membre_id) REFERENCES membre(id),
  CONSTRAINT fk_transaction_penalite FOREIGN KEY (penalite_id) REFERENCES penalite(id)
);

SET @has_idx_membre_bloque := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'membre'
    AND index_name = 'idx_membre_bloque'
);
SET @add_idx_membre_bloque := IF(@has_idx_membre_bloque = 0,
  'CREATE INDEX idx_membre_bloque ON membre(bloque)',
  'SELECT 1'
);
PREPARE add_idx_membre_bloque_stmt FROM @add_idx_membre_bloque;
EXECUTE add_idx_membre_bloque_stmt;
DEALLOCATE PREPARE add_idx_membre_bloque_stmt;

SET @has_idx_transaction_membre := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'transaction_paiement'
    AND index_name = 'idx_transaction_membre'
);
SET @add_idx_transaction_membre := IF(@has_idx_transaction_membre = 0,
  'CREATE INDEX idx_transaction_membre ON transaction_paiement(membre_id)',
  'SELECT 1'
);
PREPARE add_idx_transaction_membre_stmt FROM @add_idx_transaction_membre;
EXECUTE add_idx_transaction_membre_stmt;
DEALLOCATE PREPARE add_idx_transaction_membre_stmt;
