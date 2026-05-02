package com.audit.auditaifx.service;

import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.Recommandation;
import com.audit.auditaifx.model.StatutRapport;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.audit.auditaifx.model.Risque;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RapportService {

    private Connection conn;

    public RapportService() {
        this.conn = DatabaseConnection.getConnection();
        if (this.conn != null) {
            creerTablesInitiales();
        } else {
            System.err.println("❌ Impossible d'initialiser le service : Connexion BD nulle.");
        }
    }

    // ─── Créer tables si elles n'existent pas ────────────────

    private void creerTablesInitiales() {
        if (conn == null)
            return;
        try {
            Statement st = conn.createStatement();

            System.out.println("🔍 Vérification des tables...");
            boolean needRepair = false;
            try {
                // Vérifier la table principale
                st.executeQuery("SELECT 1 FROM audit_rapport LIMIT 1").close();
                
                // Vérifier si la nouvelle colonne existe
                try {
                    st.executeQuery("SELECT score_audit FROM audit_rapport LIMIT 1").close();
                } catch (SQLException e) {
                    System.out.println("⚠️ Colonne score_audit manquante, ajout en cours...");
                    st.execute("ALTER TABLE audit_rapport ADD COLUMN score_audit TEXT");
                }
            } catch (SQLException e) {
                String msg = e.getMessage().toLowerCase();
                if (msg.contains("doesn't exist") || msg.contains("not found")) {
                    needRepair = true;
                }
            }

            if (needRepair) {
                System.out.println("⚠️ Réparation de la base de données en cours...");
                st.execute("DROP TABLE IF EXISTS audit_reco");
                st.execute("DROP TABLE IF EXISTS audit_risque");
                st.execute("DROP TABLE IF EXISTS audit_rapport");
                System.out.println("✅ Tables supprimées pour réinitialisation.");
            }

            st.execute("""
                        CREATE TABLE IF NOT EXISTS audit_rapport (
                            id VARCHAR(36) PRIMARY KEY,
                            titre VARCHAR(200) NOT NULL,
                            auditeur VARCHAR(100) NOT NULL,
                            entite_auditee VARCHAR(100) NOT NULL,
                            statut VARCHAR(50) NOT NULL,
                            description TEXT,
                            date_creation DATE NOT NULL,
                            date_mise_a_jour DATE NOT NULL,
                            score_audit TEXT
                        ) ENGINE=InnoDB;
                    """);

            st.execute("""
                        CREATE TABLE IF NOT EXISTS audit_reco (
                            id VARCHAR(36) PRIMARY KEY,
                            rapport_id VARCHAR(36) NOT NULL,
                            description TEXT NOT NULL,
                            priorite VARCHAR(20) NOT NULL,
                            resolue BOOLEAN DEFAULT FALSE,
                            FOREIGN KEY (rapport_id)
                                REFERENCES audit_rapport(id)
                                ON DELETE CASCADE
                        ) ENGINE=InnoDB;
                    """);
            st.execute("""
                        CREATE TABLE IF NOT EXISTS audit_risque (
                            id VARCHAR(50) PRIMARY KEY,
                            rapport_id VARCHAR(50),
                            description TEXT,
                            niveau VARCHAR(20),
                            impact TEXT,
                            FOREIGN KEY (rapport_id) REFERENCES audit_rapport(id) ON DELETE CASCADE
                        )
                    """);

            System.out.println("✅ Structure de la base de données opérationnelle.");
        } catch (SQLException e) {
            System.err.println("❌ Erreur critique lors de l'initialisation DB : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ─── CRUD Rapport ─────────────────────────────────────────

    public void ajouter(RapportAudit r) {
        if (conn == null)
            return;
        String sql = """
                    INSERT INTO audit_rapport
                    (id, titre, auditeur, entite_auditee, statut, description,
                     date_creation, date_mise_a_jour, score_audit)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getId());
            ps.setString(2, r.getTitre());
            ps.setString(3, r.getAuditeur());
            ps.setString(4, r.getEntiteAuditee());
            ps.setString(5, r.getStatut().name());
            ps.setString(6, r.getDescription());
            ps.setDate(7, Date.valueOf(r.getDateCreation()));
            ps.setDate(8, Date.valueOf(r.getDateMiseAJour()));
            ps.setString(9, r.getScoreAudit());
            ps.executeUpdate();

            // Sauvegarder les recommandations
            for (Recommandation reco : r.getRecommandations()) {
                ajouterRecommandation(r.getId(), reco);
            }
        } catch (SQLException e) {
            System.err.println("Erreur ajout rapport : " + e.getMessage());
        }
    }

    public void modifier(RapportAudit r) {
        if (conn == null)
            return;
        String sql = """
                    UPDATE audit_rapport SET
                        titre=?, auditeur=?, entite_auditee=?,
                        statut=?, description=?, date_mise_a_jour=?, score_audit=?
                    WHERE id=?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getTitre());
            ps.setString(2, r.getAuditeur());
            ps.setString(3, r.getEntiteAuditee());
            ps.setString(4, r.getStatut().name());
            ps.setString(5, r.getDescription());
            ps.setDate(6, Date.valueOf(LocalDate.now()));
            ps.setString(7, r.getScoreAudit());
            ps.setString(8, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur modification rapport : " + e.getMessage());
        }
    }

    public void supprimer(RapportAudit r) {
        if (conn == null)
            return;
        String sql = "DELETE FROM audit_rapport WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur suppression rapport : " + e.getMessage());
        }
    }

    public ObservableList<RapportAudit> getTous() {
        ObservableList<RapportAudit> liste = FXCollections.observableArrayList();
        if (conn == null)
            return liste;
        String sql = "SELECT * FROM audit_rapport";
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                RapportAudit r = new RapportAudit();
                r.setId(rs.getString("id"));
                r.setTitre(rs.getString("titre"));
                r.setAuditeur(rs.getString("auditeur"));
                r.setEntiteAuditee(rs.getString("entite_auditee"));
                r.setStatut(StatutRapport.valueOf(rs.getString("statut")));
                r.setDescription(rs.getString("description"));
                r.setDateMiseAJour(rs.getDate("date_mise_a_jour").toLocalDate());
                r.setScoreAudit(rs.getString("score_audit"));
                r.setRecommandations(getRecommandations(r.getId()));
                r.setRisques(getRisques(r.getId()));
                liste.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture rapports : " + e.getMessage());
        }
        return liste;
    }

    // ─── CRUD Risque ──────────────────────────────────────────

    public void ajouterRisque(String rapportId, Risque risque) {
        if (conn == null)
            return;
        String sql = "INSERT INTO audit_risque (id, rapport_id, description, niveau, impact) VALUES (?, ?, ?, ?, ?)";
        String newId = UUID.randomUUID().toString();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newId);
            ps.setString(2, rapportId);
            ps.setString(3, risque.getDescription());
            ps.setString(4, risque.getNiveau());
            ps.setString(5, risque.getImpact());
            ps.executeUpdate();
            risque.setId(newId);
        } catch (SQLException e) {
            System.err.println("Erreur ajout risque : " + e.getMessage());
        }
    }

    public List<Risque> getRisques(String rapportId) {
        List<Risque> liste = new ArrayList<>();
        if (conn == null)
            return liste;
        String sql = "SELECT * FROM audit_risque WHERE rapport_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rapportId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Risque r = new Risque(
                        rs.getString("description"),
                        rs.getString("niveau"),
                        rs.getString("impact"));
                r.setId(rs.getString("id"));
                liste.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture risques : " + e.getMessage());
        }
        return liste;
    }

    public void supprimerRisque(String risqueId) {
        String sql = "DELETE FROM audit_risque WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, risqueId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur suppression risque : " + e.getMessage());
        }
    }

    // ─── CRUD Recommandation ──────────────────────────────────

    public void ajouterRecommandation(String rapportId, Recommandation reco) {
        if (conn == null)
            return;
        String sql = "INSERT INTO audit_reco (id, rapport_id, description, priorite, resolue) VALUES (?, ?, ?, ?, ?)";
        String newId = UUID.randomUUID().toString();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newId);
            ps.setString(2, rapportId);
            ps.setString(3, reco.getDescription());
            ps.setString(4, reco.getPriorite());
            ps.setBoolean(5, reco.isResolue());
            ps.executeUpdate();
            reco.setId(newId);
        } catch (SQLException e) {
            System.err.println("Erreur ajout recommandation : " + e.getMessage());
        }
    }

    public void modifierRecommandation(Recommandation reco) {
        String sql = """
                    UPDATE audit_reco
                    SET description=?, priorite=?, resolue=?
                    WHERE id=?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reco.getDescription());
            ps.setString(2, reco.getPriorite());
            ps.setBoolean(3, reco.isResolue());
            ps.setString(4, reco.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur modification recommandation : " + e.getMessage());
        }
    }

    public void supprimerRecommandation(String recoId) {
        String sql = "DELETE FROM audit_reco WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, recoId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur suppression recommandation : " + e.getMessage());
        }
    }

    public java.util.List<Recommandation> getRecommandations(String rapportId) {
        java.util.List<Recommandation> liste = new java.util.ArrayList<>();
        if (conn == null)
            return liste;
        String sql = "SELECT * FROM audit_reco WHERE rapport_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rapportId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recommandation reco = new Recommandation(
                        rs.getString("description"),
                        rs.getString("priorite"));
                reco.setId(rs.getString("id"));
                reco.setResolue(rs.getBoolean("resolue"));
                liste.add(reco);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lecture recommandations : " + e.getMessage());
        }
        return liste;
    }

    // ─── Données de test ──────────────────────────────────────

    public void chargerDonneesTest() {
        if (conn == null)
            return;
        // On ne charge que si la table est vide
        if (!getTous().isEmpty())
            return;

        // 1. Audit Sécurité SI
        RapportAudit r1 = new RapportAudit("Audit Sécurité SI", "Ali Ben Salem", "DSI");
        r1.setStatut(StatutRapport.EN_COURS);
        r1.setDateCreation(LocalDate.of(2026, 1, 15));
        r1.setDescription("Analyse de la sécurité périmétrique et des accès VPN.");
        r1.ajouterRecommandation(new Recommandation("Mettre à jour les pare-feux", "Haute"));
        r1.ajouterRecommandation(new Recommandation("Activer l'authentification 2FA", "Haute"));
        r1.ajouterRecommandation(new Recommandation("Auditer les accès administrateurs", "Moyenne"));
        ajouter(r1);

        // 2. Audit Financier Q1
        RapportAudit r2 = new RapportAudit("Audit Financier Q1", "Sara Mansour", "Finance");
        r2.setStatut(StatutRapport.FINALISE);
        r2.setDateCreation(LocalDate.of(2026, 1, 25));
        r2.setDescription("Vérification des processus de facturation manuels.");
        r2.ajouterRecommandation(new Recommandation("Réviser la politique des dépenses", "Moyenne"));
        r2.ajouterRecommandation(new Recommandation("Automatiser les rapports comptables", "Faible"));
        r2.ajouterRecommandation(new Recommandation("Mettre en place un contrôle budgétaire mensuel", "Haute"));
        ajouter(r2);

        // 3. Audit RH 2024
        RapportAudit r3 = new RapportAudit("Audit RH 2024", "Mohamed Triki", "Ressources Humaines");
        r3.setStatut(StatutRapport.BROUILLON);
        r3.setDateCreation(LocalDate.of(2026, 2, 5));
        r3.setDescription("Audit des dossiers du personnel et stockage physique.");
        r3.ajouterRecommandation(new Recommandation("Mettre à jour les contrats", "Haute"));
        r3.ajouterRecommandation(new Recommandation("Digitaliser les dossiers", "Faible"));
        r3.ajouterRecommandation(new Recommandation("Former au RGPD", "Moyenne"));
        r3.ajouterRecommandation(new Recommandation("Réviser la politique de recrutement", "Moyenne"));
        ajouter(r3);

        // 4. Audit Qualité ISO 9001
        RapportAudit r4 = new RapportAudit("Audit Qualité ISO 9001", "Fatma Riahi", "Production");
        r4.setStatut(StatutRapport.EN_COURS);
        r4.setDateCreation(LocalDate.of(2026, 2, 12));
        r4.setDescription("Évaluation de la traçabilité des pièces sur la chaîne.");
        r4.ajouterRecommandation(new Recommandation("Système de traçabilité", "Haute"));
        r4.ajouterRecommandation(new Recommandation("Revoir contrôle qualité", "Moyenne"));
        r4.ajouterRecommandation(new Recommandation("Former les opérateurs aux normes ISO", "Haute"));
        ajouter(r4);

        // 5. Audit Performance IT
        RapportAudit r5 = new RapportAudit("Audit Performance IT", "Jean Dupont", "IT");
        r5.setStatut(StatutRapport.FINALISE);
        r5.setDateCreation(LocalDate.of(2026, 2, 28));
        r5.setDescription("Optimisation des temps de réponse des serveurs critiques.");
        r5.ajouterRecommandation(new Recommandation("Migrer vers le Cloud", "Moyenne"));
        r5.ajouterRecommandation(new Recommandation("Optimiser les requêtes SQL", "Haute"));
        r5.ajouterRecommandation(new Recommandation("Mettre en place un système de monitoring", "Haute"));
        r5.ajouterRecommandation(new Recommandation("Planifier les sauvegardes automatiques", "Moyenne"));
        ajouter(r5);

        // 6. Audit Conformité RGPD
        RapportAudit r6 = new RapportAudit("Audit Conformité RGPD", "Sophie Laurent", "Juridique");
        r6.setStatut(StatutRapport.EN_COURS);
        r6.setDateCreation(LocalDate.of(2026, 3, 2));
        r6.setDescription("Vérification de la gestion des données personnelles.");
        r6.ajouterRecommandation(new Recommandation("Nommer un DPO", "Haute"));
        r6.ajouterRecommandation(new Recommandation("Mettre à jour les mentions légales", "Faible"));
        r6.ajouterRecommandation(new Recommandation("Documenter les flux de données", "Moyenne"));
        ajouter(r6);

        // 7. Audit Logistique
        RapportAudit r7 = new RapportAudit("Audit Logistique", "Marc Durand", "Entrepôt");
        r7.setStatut(StatutRapport.BROUILLON);
        r7.setDateCreation(LocalDate.of(2026, 3, 15));
        r7.setDescription("Analyse de la gestion des stocks et des flux.");
        r7.ajouterRecommandation(new Recommandation("Installer un WMS", "Haute"));
        r7.ajouterRecommandation(new Recommandation("Réorganiser le zonage", "Moyenne"));
        r7.ajouterRecommandation(new Recommandation("Automatiser les inventaires", "Haute"));
        ajouter(r7);

        // 8. Audit Marketing Digital
        RapportAudit r8 = new RapportAudit("Audit Marketing Digital", "Kevin Martin", "Marketing");
        r8.setStatut(StatutRapport.FINALISE);
        r8.setDateCreation(LocalDate.of(2026, 3, 22));
        r8.setDescription("Évaluation du ROI des campagnes publicitaires.");
        r8.ajouterRecommandation(new Recommandation("Changer d'agence média", "Faible"));
        r8.ajouterRecommandation(new Recommandation("Améliorer le tracking", "Moyenne"));
        r8.ajouterRecommandation(new Recommandation("Développer la stratégie SEO", "Haute"));
        ajouter(r8);

        // 9. Audit Maintenance
        RapportAudit r9 = new RapportAudit("Audit Maintenance", "Paul Lefebvre", "Technique");
        r9.setStatut(StatutRapport.EN_COURS);
        r9.setDateCreation(LocalDate.of(2026, 4, 1));
        r9.setDescription("Vérification du plan de maintenance préventive.");
        r9.ajouterRecommandation(new Recommandation("Remplacer les machines obsolètes", "Haute"));
        r9.ajouterRecommandation(new Recommandation("Planifier les arrêts techniques", "Moyenne"));
        r9.ajouterRecommandation(new Recommandation("Mettre en place une GMAO", "Haute"));
        ajouter(r9);

        // 10. Audit Sécurité Physique
        RapportAudit r10 = new RapportAudit("Audit Sécurité Physique", "Eric Petit", "Sécurité");
        r10.setStatut(StatutRapport.FINALISE);
        r10.setDateCreation(LocalDate.of(2026, 4, 10));
        r10.setDescription("Contrôle des accès aux bâtiments et vidéosurveillance.");
        r10.ajouterRecommandation(new Recommandation("Ajouter des caméras", "Moyenne"));
        r10.ajouterRecommandation(new Recommandation("Badgeage biométrique", "Haute"));
        r10.ajouterRecommandation(new Recommandation("Remplacer les serrures", "Moyenne"));
        ajouter(r10);
    }

    public void chargerDonneesSupplementaires() {
        if (conn == null)
            return;

        // 11. Audit Cybersécurité Avancé
        RapportAudit r11 = new RapportAudit("Audit Cybersécurité Avancé", "Leila Bensalem", "DSI");
        r11.setStatut(StatutRapport.EN_COURS);
        r11.setDateCreation(LocalDate.of(2026, 1, 5));
        r11.setDescription("Analyse des vulnérabilités des applications web et API exposées.");
        r11.ajouterRecommandation(new Recommandation("Effectuer un pentest annuel", "Haute"));
        r11.ajouterRecommandation(new Recommandation("Corriger les failles XSS détectées", "Haute"));
        r11.ajouterRecommandation(new Recommandation("Mettre en place un WAF", "Haute"));
        r11.ajouterRecommandation(new Recommandation("Chiffrer les communications internes", "Moyenne"));
        ajouter(r11);

        // 12. Audit Achats & Fournisseurs
        RapportAudit r12 = new RapportAudit("Audit Achats & Fournisseurs", "Nadia Gharbi", "Achats");
        r12.setStatut(StatutRapport.BROUILLON);
        r12.setDateCreation(LocalDate.of(2026, 1, 20));
        r12.setDescription("Évaluation des processus d'appel d'offres et de sélection fournisseurs.");
        r12.ajouterRecommandation(new Recommandation("Centraliser les contrats fournisseurs", "Haute"));
        r12.ajouterRecommandation(new Recommandation("Créer une grille d'évaluation fournisseurs", "Moyenne"));
        r12.ajouterRecommandation(new Recommandation("Réduire les délais de paiement", "Faible"));
        ajouter(r12);

        // 13. Audit Comptabilité Analytique
        RapportAudit r13 = new RapportAudit("Audit Comptabilité Analytique", "Karim Haddad", "Finance");
        r13.setStatut(StatutRapport.FINALISE);
        r13.setDateCreation(LocalDate.of(2026, 2, 10));
        r13.setDescription("Révision du système de comptabilité analytique et des centres de coûts.");
        r13.ajouterRecommandation(new Recommandation("Revoir l'affectation des centres de coûts", "Haute"));
        r13.ajouterRecommandation(new Recommandation("Intégrer un outil de BI financier", "Moyenne"));
        r13.ajouterRecommandation(new Recommandation("Former les équipes aux nouveaux processus", "Faible"));
        r13.ajouterRecommandation(new Recommandation("Rapprocher les données ERP mensuellement", "Haute"));
        ajouter(r13);

        // 14. Audit Continuité d'Activité
        RapportAudit r14 = new RapportAudit("Audit Continuité d'Activité (PCA)", "Thomas Bernard", "Direction");
        r14.setStatut(StatutRapport.EN_COURS);
        r14.setDateCreation(LocalDate.of(2026, 2, 20));
        r14.setDescription("Évaluation du Plan de Continuité d'Activité et des procédures de reprise.");
        r14.ajouterRecommandation(new Recommandation("Tester le PCA deux fois par an", "Haute"));
        r14.ajouterRecommandation(new Recommandation("Documenter les scénarios de crise", "Haute"));
        r14.ajouterRecommandation(new Recommandation("Désigner un responsable PCA", "Moyenne"));
        ajouter(r14);

        // 15. Audit Formation & Compétences
        RapportAudit r15 = new RapportAudit("Audit Formation & Compétences", "Amina Berrada", "Ressources Humaines");
        r15.setStatut(StatutRapport.BROUILLON);
        r15.setDateCreation(LocalDate.of(2026, 3, 1));
        r15.setDescription("Analyse du plan de formation et de l'adéquation compétences/postes.");
        r15.ajouterRecommandation(new Recommandation("Identifier les écarts de compétences", "Haute"));
        r15.ajouterRecommandation(new Recommandation("Mettre en place un plan de succession", "Moyenne"));
        r15.ajouterRecommandation(new Recommandation("Développer les formations e-learning", "Faible"));
        r15.ajouterRecommandation(new Recommandation("Évaluer l'efficacité des formations", "Moyenne"));
        ajouter(r15);

        // 16. Audit ERP & Systèmes Métier
        RapportAudit r16 = new RapportAudit("Audit ERP & Systèmes Métier", "Julien Moreau", "IT");
        r16.setStatut(StatutRapport.EN_COURS);
        r16.setDateCreation(LocalDate.of(2026, 3, 18));
        r16.setDescription("Analyse de l'utilisation et de la configuration du système ERP.");
        r16.ajouterRecommandation(new Recommandation("Nettoyer les comptes utilisateurs inactifs", "Haute"));
        r16.ajouterRecommandation(new Recommandation("Mettre à jour vers la dernière version ERP", "Haute"));
        r16.ajouterRecommandation(new Recommandation("Automatiser les rapports de clôture", "Moyenne"));
        ajouter(r16);

        // 17. Audit Développement Durable
        RapportAudit r17 = new RapportAudit("Audit Développement Durable (RSE)", "Claire Fontaine", "Direction");
        r17.setStatut(StatutRapport.FINALISE);
        r17.setDateCreation(LocalDate.of(2026, 4, 3));
        r17.setDescription("Évaluation des pratiques RSE et de l'empreinte carbone de l'organisation.");
        r17.ajouterRecommandation(new Recommandation("Mesurer l'empreinte carbone annuelle", "Haute"));
        r17.ajouterRecommandation(new Recommandation("Réduire les déchets de production de 20%", "Moyenne"));
        r17.ajouterRecommandation(new Recommandation("Déployer des panneaux solaires", "Faible"));
        r17.ajouterRecommandation(new Recommandation("Sensibiliser les équipes aux gestes éco-responsables", "Faible"));
        ajouter(r17);

        // 18. Audit Relation Client
        RapportAudit r18 = new RapportAudit("Audit Relation Client (CRM)", "Youssef Alami", "Commercial");
        r18.setStatut(StatutRapport.EN_COURS);
        r18.setDateCreation(LocalDate.of(2026, 4, 12));
        r18.setDescription("Évaluation des processus de gestion de la relation client et du CRM.");
        r18.ajouterRecommandation(new Recommandation("Centraliser les données clients dans le CRM", "Haute"));
        r18.ajouterRecommandation(new Recommandation("Automatiser les relances commerciales", "Moyenne"));
        r18.ajouterRecommandation(new Recommandation("Former l'équipe commerciale au CRM", "Moyenne"));
        r18.ajouterRecommandation(new Recommandation("Améliorer les tableaux de bord de vente", "Haute"));
        ajouter(r18);

        System.out.println("✅ 8 rapports supplémentaires chargés avec succès.");
    }

    public void injecterNouveauxRapports() {
        if (conn == null) return;
        
        // 19. Audit Gouvernance IT
        RapportAudit r19 = new RapportAudit("Audit Gouvernance IT", "Sophie Chen", "DSI");
        r19.setStatut(StatutRapport.EN_COURS);
        r19.setDateCreation(LocalDate.of(2026, 3, 10));
        r19.setDescription("Évaluation de l'alignement stratégique de l'IT avec les objectifs métier.");
        r19.ajouterRecommandation(new Recommandation("Mettre en place un comité de pilotage IT", "Haute"));
        r19.ajouterRecommandation(new Recommandation("Définir des KPIs de performance", "Moyenne"));
        ajouter(r19);

        // 20. Audit Gestion des Actifs
        RapportAudit r20 = new RapportAudit("Audit Gestion des Actifs", "Luc Petit", "Logistique");
        r20.setStatut(StatutRapport.FINALISE);
        r20.setDateCreation(LocalDate.of(2026, 4, 5));
        r20.setDescription("Inventaire et suivi du cycle de vie des actifs matériels.");
        r20.ajouterRecommandation(new Recommandation("Automatiser le suivi des amortissements", "Moyenne"));
        r20.ajouterRecommandation(new Recommandation("Sécuriser le stockage des pièces de rechange", "Haute"));
        ajouter(r20);

        // 21. Audit Sécurité Cloud
        RapportAudit r21 = new RapportAudit("Audit Sécurité Cloud", "Emma Wilson", "IT Operations");
        r21.setStatut(StatutRapport.EN_COURS);
        r21.setDateCreation(LocalDate.of(2026, 4, 20));
        r21.setDescription("Vérification de la configuration de sécurité des environnements AWS et Azure.");
        r21.ajouterRecommandation(new Recommandation("Restreindre les accès S3 publics", "Haute"));
        r21.ajouterRecommandation(new Recommandation("Activer GuardDuty sur toutes les régions", "Moyenne"));
        ajouter(r21);

        // 22. Audit Conformité Fiscale
        RapportAudit r22 = new RapportAudit("Audit Conformité Fiscale", "Jean-Paul Sartre", "Finance");
        r22.setStatut(StatutRapport.BROUILLON);
        r22.setDateCreation(LocalDate.of(2026, 4, 25));
        r22.setDescription("Révision des déclarations fiscales et conformité aux nouvelles régulations.");
        r22.ajouterRecommandation(new Recommandation("Audit des crédits d'impôt recherche", "Haute"));
        ajouter(r22);

        System.out.println("✅ Nouveaux rapports injectés pour le dashboard.");
    }

    public void initialiserDonneesSiVide() {
        if (getTous().isEmpty()) {
            System.out.println("🌱 Initialisation des données de démonstration...");
            chargerDonneesTest();
            chargerDonneesSupplementaires();
            injecterNouveauxRapports();
        }
    }
}