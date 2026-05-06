CREATE TABLE IF NOT EXISTS entreprises (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    matricule_fiscale VARCHAR(255),
    secteur VARCHAR(255),
    taille VARCHAR(255),
    pays VARCHAR(255),
    email VARCHAR(255),
    telephone VARCHAR(255),
    adresse TEXT,
    statut VARCHAR(50) DEFAULT 'en_attente',
    owner_id INT,
    latitude DOUBLE,
    longitude DOUBLE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS documents (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    path TEXT,
    entreprise_id INT,
    FOREIGN KEY (entreprise_id) REFERENCES entreprises(id) ON DELETE CASCADE
);
