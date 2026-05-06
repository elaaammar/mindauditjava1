package services;

import models.QuestionAudit;
import models.ReponseQuestion;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ScoringService — compares user answer vs admin's correct answer
 * and assigns a score (0–100) per question.
 *
 * Algorithm:
 *  - Exact match (case-insensitive) → 100 pts
 *  - Partial keyword match         → proportional score (0–90 pts)
 *  - No answer / no correct answer → 0 pts
 */
public class ScoringService {

    // Words to ignore in comparison (stop words)
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "le", "la", "les", "de", "du", "des", "un", "une", "et", "ou", "est",
        "en", "que", "qui", "se", "à", "au", "aux", "par", "sur", "dans", "the",
        "is", "a", "an", "of", "to", "and", "or", "in", "on", "it", "be"
    ));

    /**
     * Calculates a similarity score (0 to 100) between user answer and correct answer.
     */
    public int calculateScore(String bonneReponse, String userReponse) {
        if (bonneReponse == null || bonneReponse.trim().isEmpty()) return -1; // not graded
        if (userReponse == null || userReponse.trim().isEmpty()) return 0;

        String correct = bonneReponse.trim().toLowerCase();
        String user = userReponse.trim().toLowerCase();

        // Exact match
        if (correct.equals(user)) return 100;

        // Keyword matching
        Set<String> correctWords = getKeywords(correct);
        Set<String> userWords = getKeywords(user);

        if (correctWords.isEmpty()) return 0;

        // Count how many keywords from correct answer the user used
        long matched = correctWords.stream().filter(userWords::contains).count();
        int score = (int) Math.round((double) matched / correctWords.size() * 100);

        return Math.min(score, 99); // max 99 for partial (100 = exact only)
    }

    private Set<String> getKeywords(String text) {
        Set<String> words = new HashSet<>();
        for (String word : text.split("\\s+|[,;.!?'\"()\\[\\]]")) {
            String clean = word.toLowerCase().trim();
            if (!clean.isEmpty() && !STOP_WORDS.contains(clean) && clean.length() > 2) {
                words.add(clean);
            }
        }
        return words;
    }

    /**
     * Returns a label with color for UI display based on score.
     */
    public String getScoreLabel(int score) {
        if (score == -1) return "Non noté";
        if (score == 100) return "✅ 100/100 (Parfait)";
        if (score >= 75) return "🟡 " + score + "/100 (Bien)";
        if (score >= 50) return "🟠 " + score + "/100 (Moyen)";
        if (score > 0)   return "🔴 " + score + "/100 (Faible)";
        return "❌ 0/100 (Aucune réponse)";
    }

    /**
     * Returns CSS color for the score.
     */
    public String getScoreColor(int score) {
        if (score == -1) return "#95a5a6";
        if (score == 100) return "#27ae60";
        if (score >= 75) return "#f39c12";
        if (score >= 50) return "#e67e22";
        return "#e74c3c";
    }

    /**
     * Calculates the global average score for a report.
     * Returns a string like "75/100 — Bien"
     */
    public String calculateReportScore(List<QuestionAudit> questions, int rapportId, ReponseQuestionService reponseService) {
        if (questions == null || questions.isEmpty()) return "Aucune question";

        int total = 0;
        int graded = 0;

        for (QuestionAudit q : questions) {
            if (q.getBonneReponse() == null || q.getBonneReponse().isEmpty()) continue;
            ReponseQuestion rq = reponseService.getAnswerForQuestionAndReport(q.getId(), rapportId);
            String userAnswer = (rq != null) ? rq.getReponse() : "";
            int score = calculateScore(q.getBonneReponse(), userAnswer);
            if (score >= 0) {
                total += score;
                graded++;
            }
        }

        if (graded == 0) return "Aucune note disponible";
        int avg = total / graded;

        String mention;
        if (avg >= 90) mention = "🏆 Excellent";
        else if (avg >= 75) mention = "✅ Bien";
        else if (avg >= 50) mention = "🟡 Passable";
        else mention = "❌ Insuffisant";

        return avg + "/100 — " + mention + "  (" + graded + " question(s) notée(s))";
    }
}
