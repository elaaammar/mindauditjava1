package com.audit.auditaifx.service;

import com.audit.auditaifx.model.RapportAudit;
import com.audit.auditaifx.model.Recommandation;
import com.audit.auditaifx.model.Risque;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIService {

    private final Random random = new Random();

    /**
     * Analyse les données d'un rapport et génère une recommandation pertinente.
     */
    public Recommandation genererRecommandation(RapportAudit rapport) {
        if (rapport == null)
            return null;

        String description = rapport.getDescription() != null ? rapport.getDescription().toLowerCase() : "";
        String titre = rapport.getTitre() != null ? rapport.getTitre().toLowerCase() : "";

        String recoDesc;
        String priorite = "Moyenne";

        // Logique "IA" simple basée sur des mots-clés
        if (description.contains("sécurité") || titre.contains("sécurité") || description.contains("security")) {
            recoDesc = "Renforcer le contrôle d'accès et implémenter une authentification multifacteur (MFA) sur les systèmes critiques.";
            priorite = "Haute";
        } else if (description.contains("données") || description.contains("data")
                || description.contains("stockage")) {
            recoDesc = "Mettre en place un plan de sauvegarde automatisé et chiffrer les données au repos.";
            priorite = "Haute";
        } else if (description.contains("réseau") || description.contains("network")) {
            recoDesc = "Isoler les environnements de test de la production via une segmentation réseau stricte.";
            priorite = "Moyenne";
        } else if (description.contains("processus") || description.contains("manuel")) {
            recoDesc = "Automatiser les workflows d'approbation pour réduire les risques d'erreurs humaines.";
            priorite = "Basse";
        } else {
            // Recommandations génériques si aucun mot-clé n'est trouvé
            String[] generiques = {
                    "Réaliser une revue trimestrielle des habilitations utilisateurs.",
                    "Mettre à jour la documentation des procédures opérationnelles standard.",
                    "Former le personnel aux bonnes pratiques d'hygiène informatique.",
                    "Optimiser le suivi des correctifs de vulnérabilité logicielle."
            };
            recoDesc = generiques[random.nextInt(generiques.length)];
        }

        Recommandation reco = new Recommandation();
        reco.setDescription("[IA] " + recoDesc);
        reco.setPriorite(priorite);
        reco.setResolue(false);

        return reco;
    }

    public Risque genererRisque(RapportAudit rapport) {
        if (rapport == null) return null;

        String description = (rapport.getDescription() != null ? rapport.getDescription() : "").toLowerCase();
        int nbRecos = (rapport.getRecommandations() != null ? rapport.getRecommandations().size() : 0);
        java.time.LocalDate date = rapport.getDateCreation();

        String risqueDesc = "Risque identifié suite à l'analyse des recommandations.";
        String niveau = "Moyen";
        String impact = "Impact sur la continuité de service.";

        // Logique basée sur les recommandations
        if (nbRecos > 8) {
            risqueDesc = "Risque de surcharge opérationnelle dû au volume élevé de recommandations.";
            niveau = "Élevé";
            impact = "Retard dans la mise en œuvre des contrôles critiques.";
        } 
        // Logique basée sur la date (plus de 6 mois)
        else if (date != null && date.isBefore(java.time.LocalDate.now().minusMonths(6))) {
            risqueDesc = "Risque d'obsolescence des données d'audit (rapport de plus de 6 mois).";
            niveau = "Moyen";
            impact = "Décisions basées sur des informations potentiellement périmées.";
        }
        // Logique basée sur le texte
        else if (description.contains("sécurité") || description.contains("mfa") || description.contains("accès")) {
            risqueDesc = "Risque de compromission des identités et accès non autorisés.";
            niveau = "Critique";
            impact = "Exposition des données sensibles et violation de conformité.";
        } else if (description.contains("sauvegarde") || description.contains("backup")) {
            risqueDesc = "Risque de perte de données par manque de résilience.";
            niveau = "Élevé";
            impact = "Interruption prolongée de l'activité en cas d'incident majeur.";
        }

        return new Risque("[IA] " + risqueDesc, niveau, impact);
    }

    /**
     * Analyse tous les rapports et retourne celui à traiter en priorité.
     * Score basé sur : recommandations non résolues (x3 si Haute, x2 si Moyenne),
     * nombre de risques (x4), et statut (EN_COURS bonus).
     */
    public PrioriteResult calculeRapportPrioritaire(List<com.audit.auditaifx.model.RapportAudit> rapports) {
        if (rapports == null || rapports.isEmpty()) return null;

        com.audit.auditaifx.model.RapportAudit best = null;
        int bestScore = -1;
        String bestRaison = "";

        for (com.audit.auditaifx.model.RapportAudit r : rapports) {
            int score = 0;
            int nbHaute = 0, nbMoyenne = 0, nbNonResolues = 0;

            for (com.audit.auditaifx.model.Recommandation reco : r.getRecommandations()) {
                if (!reco.isResolue()) {
                    nbNonResolues++;
                    String p = reco.getPriorite() != null ? reco.getPriorite().toLowerCase() : "";
                    if (p.contains("haut")) { score += 3; nbHaute++; }
                    else if (p.contains("moy")) { score += 2; nbMoyenne++; }
                    else score += 1;
                }
            }

            // Risques comptent beaucoup
            int nbRisques = r.getRisques() != null ? r.getRisques().size() : 0;
            score += nbRisques * 4;

            // Bonus statut EN_COURS (déjà en cours = urgent)
            if (r.getStatut() == com.audit.auditaifx.model.StatutRapport.EN_COURS) score += 5;
            // Brouillon = encore plus urgent car jamais commencé
            if (r.getStatut() == com.audit.auditaifx.model.StatutRapport.BROUILLON) score += 3;

            if (score > bestScore) {
                bestScore = score;
                best = r;
                // Construire l'explication
                List<String> raisons = new java.util.ArrayList<>();
                if (nbHaute > 0) raisons.add(nbHaute + " reco(s) haute priorité non résolue(s)");
                if (nbMoyenne > 0) raisons.add(nbMoyenne + " reco(s) priorité moyenne en attente");
                if (nbRisques > 0) raisons.add(nbRisques + " risque(s) détecté(s)");
                if (r.getStatut() == com.audit.auditaifx.model.StatutRapport.EN_COURS) raisons.add("statut EN COURS");
                if (r.getStatut() == com.audit.auditaifx.model.StatutRapport.BROUILLON) raisons.add("encore en BROUILLON");
                bestRaison = raisons.isEmpty() ? "Rapport nécessitant une attention." : String.join(" • ", raisons);
            }
        }

        return best != null ? new PrioriteResult(best, bestScore, bestRaison) : null;
    }

    public String calculerScoreAudit(RapportAudit rapport) {
        if (rapport == null) return "{\"error\": \"Rapport nul\"}";

        String description = (rapport.getDescription() != null ? rapport.getDescription() : "").toLowerCase();
        int recos = (rapport.getRecommandations() != null ? rapport.getRecommandations().size() : 0);
        int risques = (rapport.getRisques() != null ? rapport.getRisques().size() : 0);

        // Calcul simulé
        double c = 7.0 + (description.length() > 200 ? 2.0 : 0.5); // Complétude
        double cf = 6.0 + (recos > 5 ? 3.0 : 1.0);                  // Conformité
        double d = 5.0 + (description.contains("procédure") ? 4.0 : 1.0); // Documentation
        double s = 4.0 + (recos > 0 && rapport.getRecommandations().get(0).isResolue() ? 5.0 : 1.0); // Suivi
        double r = 8.0 - (risques * 0.5);                           // Réactivité

        // Limiter à 10
        c = Math.min(10, c); cf = Math.min(10, cf); d = Math.min(10, d); s = Math.min(10, s); r = Math.min(10, r);

        double global = (c + cf + d + s + r) / 5.0;
        String verdict = (global >= 7 ? "Performance satisfaisante." : (global >= 5 ? "Améliorations nécessaires." : "Audit critique."));

        return String.format(java.util.Locale.US,
            "{\"completude\": %.1f, \"conformite\": %.1f, \"documentation\": %.1f, \"suivi\": %.1f, \"reactivite\": %.1f, \"global\": %.1f, \"verdict\": \"%s\"}",
            c, cf, d, s, r, global, verdict);
    }

    /** Résultat de l'analyse de priorité IA */
    public static class PrioriteResult {
        public final com.audit.auditaifx.model.RapportAudit rapport;
        public final int score;
        public final String raison;

        public PrioriteResult(com.audit.auditaifx.model.RapportAudit rapport, int score, String raison) {
            this.rapport = rapport;
            this.score = score;
            this.raison = raison;
        }
    }
}