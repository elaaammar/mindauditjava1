package services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import models.Rapport;
import models.QuestionAudit;
import models.ReponseQuestion;

import java.awt.Color;
import java.io.FileOutputStream;
import java.util.List;

public class PDFService {

    private final ScoringService scoringService = new ScoringService();

    public void generateReport(Rapport rapport) {
        Document document = new Document();
        try {
            String fileName = "Audit_Report_" + rapport.getId() + ".pdf";
            
            // Check if file is locked (already open)
            java.io.File fileTest = new java.io.File(fileName);
            if (fileTest.exists() && !fileTest.renameTo(fileTest)) {
                // If we can't rename it to itself, it's open. Append timestamp.
                fileName = "Audit_Report_" + rapport.getId() + "_" + System.currentTimeMillis() + ".pdf";
            }

            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // ── Fonts ──────────────────────────────────────────────
            Font titleFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.WHITE);
            Font headerFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(44, 62, 80));
            Font normalFont  = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);
            Font boldFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY);
            Font greenFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(39, 174, 96));
            Font redFont     = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(192, 57, 43));
            Font orangeFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, new Color(230, 126, 34));
            Font scoreFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.WHITE);
            Font colHeader   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);

            // ── Title Banner ───────────────────────────────────────
            PdfPTable banner = new PdfPTable(1);
            banner.setWidthPercentage(100);
            PdfPCell bannerCell = new PdfPCell(new Phrase("  RAPPORT D'AUDIT  ", titleFont));
            bannerCell.setBackgroundColor(new Color(44, 62, 80));
            bannerCell.setBorder(Rectangle.NO_BORDER);
            bannerCell.setPadding(14);
            bannerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            banner.addCell(bannerCell);
            document.add(banner);
            document.add(new Paragraph(" "));

            // ── Info Section ───────────────────────────────────────
            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{1, 2});
            addInfoRow(infoTable, "Titre", rapport.getTitle(), boldFont, normalFont);
            addInfoRow(infoTable, "Catégorie", rapport.getType(), boldFont, normalFont);
            addInfoRow(infoTable, "Date", rapport.getDate(), boldFont, normalFont);
            addInfoRow(infoTable, "Description", rapport.getDescription(), boldFont, normalFont);
            document.add(infoTable);
            document.add(new Paragraph(" "));

            // ── Questions Table ────────────────────────────────────
            Paragraph sectionTitle = new Paragraph("DÉTAIL DES QUESTIONS & SCORING", headerFont);
            sectionTitle.setSpacingBefore(10);
            sectionTitle.setSpacingAfter(8);
            document.add(sectionTitle);

            List<QuestionAudit> questions = rapport.getQuestions();
            int totalScore = 0;
            int gradedCount = 0;

            if (questions == null || questions.isEmpty()) {
                document.add(new Paragraph("Aucune question associée à ce rapport.", normalFont));
            } else {
                ReponseQuestionService reponseService = new ReponseQuestionService();

                // Table with 5 columns: #, Question, Bonne Réponse, Réponse User, Score
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{0.4f, 2.5f, 2f, 2f, 1.1f});

                // Header row
                String[] headers = {"#", "Question", "Bonne Réponse", "Réponse Utilisateur", "Score"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, colHeader));
                    cell.setBackgroundColor(new Color(52, 152, 219));
                    cell.setPadding(7);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                for (int i = 0; i < questions.size(); i++) {
                    QuestionAudit q = questions.get(i);
                    ReponseQuestion resp = reponseService.getAnswerForQuestionAndReport(q.getId(), rapport.getId());
                    String userAnswer   = (resp != null && resp.getReponse() != null) ? resp.getReponse() : "—";
                    String bonneReponse = (q.getBonneReponse() != null && !q.getBonneReponse().isEmpty()) ? q.getBonneReponse() : "Non définie";

                    int score = scoringService.calculateScore(q.getBonneReponse(), userAnswer.equals("—") ? "" : userAnswer);

                    // Row background alternation
                    Color rowBg = (i % 2 == 0) ? Color.WHITE : new Color(236, 240, 241);

                    addTableCell(table, String.valueOf(i + 1), normalFont, rowBg, Element.ALIGN_CENTER);
                    addTableCell(table, q.getContent(), normalFont, rowBg, Element.ALIGN_LEFT);
                    addTableCell(table, bonneReponse, normalFont, rowBg, Element.ALIGN_LEFT);
                    addTableCell(table, userAnswer, normalFont, rowBg, Element.ALIGN_LEFT);

                    // Score cell with color
                    String scoreLabel;
                    Color scoreColor;
                    Font scoreCellFont;
                    if (score == -1) {
                        scoreLabel = "—";
                        scoreColor = new Color(149, 165, 166);
                        scoreCellFont = normalFont;
                    } else if (score == 100) {
                        scoreLabel = "100 ✓";
                        scoreColor = new Color(39, 174, 96);
                        scoreCellFont = greenFont;
                        totalScore += score; gradedCount++;
                    } else if (score >= 75) {
                        scoreLabel = score + "/100";
                        scoreColor = new Color(243, 156, 18);
                        scoreCellFont = orangeFont;
                        totalScore += score; gradedCount++;
                    } else if (score >= 50) {
                        scoreLabel = score + "/100";
                        scoreColor = new Color(230, 126, 34);
                        scoreCellFont = orangeFont;
                        totalScore += score; gradedCount++;
                    } else {
                        scoreLabel = score + "/100";
                        scoreColor = new Color(192, 57, 43);
                        scoreCellFont = redFont;
                        if (score >= 0) { totalScore += score; gradedCount++; }
                    }

                    PdfPCell scoreCell = new PdfPCell(new Phrase(scoreLabel, scoreCellFont));
                    scoreCell.setBackgroundColor(rowBg);
                    scoreCell.setPadding(6);
                    scoreCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(scoreCell);
                }

                document.add(table);
                document.add(new Paragraph(" "));

                // ── Final Score Banner ─────────────────────────────
                int finalScore = (gradedCount > 0) ? totalScore / gradedCount : 0;
                String mention;
                Color mentionColor;
                if (finalScore >= 90)      { mention = "🏆 EXCELLENT";   mentionColor = new Color(39, 174, 96); }
                else if (finalScore >= 75) { mention = "✅ BIEN";         mentionColor = new Color(39, 174, 96); }
                else if (finalScore >= 50) { mention = "🟡 PASSABLE";     mentionColor = new Color(230, 126, 34); }
                else                       { mention = "❌ INSUFFISANT";  mentionColor = new Color(192, 57, 43); }

                PdfPTable scoreBanner = new PdfPTable(2);
                scoreBanner.setWidthPercentage(100);
                scoreBanner.setWidths(new float[]{2, 1});

                PdfPCell mentionCell = new PdfPCell(new Phrase("Résultat Global : " + mention, scoreFont));
                mentionCell.setBackgroundColor(new Color(44, 62, 80));
                mentionCell.setPadding(12);
                mentionCell.setBorder(Rectangle.NO_BORDER);
                scoreBanner.addCell(mentionCell);

                PdfPCell scoreNumCell = new PdfPCell(new Phrase(finalScore + " / 100", scoreFont));
                scoreNumCell.setBackgroundColor(mentionColor);
                scoreNumCell.setPadding(12);
                scoreNumCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                scoreNumCell.setBorder(Rectangle.NO_BORDER);
                scoreBanner.addCell(scoreNumCell);

                document.add(scoreBanner);

                Paragraph detail = new Paragraph("Basé sur " + gradedCount + " question(s) corrigée(s) sur " + questions.size() + " au total.", normalFont);
                detail.setSpacingBefore(5);
                document.add(detail);
            }

            // ── QR Code Section (Title, Score, Date) ──────────────────
            document.add(new Paragraph(" "));
            Paragraph qrTitle = new Paragraph("CERTIFICATE QR CODE:", boldFont);
            qrTitle.setSpacingAfter(5);
            document.add(qrTitle);
            
            // Calculate final score again for the QR text
            int finalScoreVal = (gradedCount > 0) ? totalScore / gradedCount : 0;

            QRCodeService qrService = new QRCodeService();
            String qrText = "📜 AUDIT REPORT\n" +
                           "--------------------------\n" +
                           "📍 Title: " + rapport.getTitle() + "\n" +
                           "🏆 Final Score: " + finalScoreVal + "/100\n" +
                           "📅 Date: " + rapport.getDate() + "\n" +
                           "--------------------------\n" +
                           "Verified by AuditPro System";
            
            byte[] qrImageBytes = qrService.generateQRCodeImage(qrText, 140, 140);
            
            if (qrImageBytes != null) {
                Image qrImage = Image.getInstance(qrImageBytes);
                qrImage.setAlignment(Element.ALIGN_LEFT);
                qrImage.setSpacingBefore(10);
                document.add(qrImage);
            }

            document.close();

            // Auto-open PDF
            java.io.File pdfFile = new java.io.File(fileName);
            if (pdfFile.exists() && java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(pdfFile);
            }

        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void addInfoRow(PdfPTable table, String label, String value, Font labelFont, Font valueFont) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBackgroundColor(new Color(236, 240, 241));
        labelCell.setPadding(6);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value != null ? value : "—", valueFont));
        valueCell.setPadding(6);
        table.addCell(valueCell);
    }

    private void addTableCell(PdfPTable table, String text, Font font, Color bg, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "—", font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        table.addCell(cell);
    }
}
