package com.gestion.util;

import com.gestion.entity.Document;
import com.gestion.entity.Entreprise;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PdfExporter {

    public static void exportEntreprise(Entreprise e, List<Document> docs, String path) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            float pageWidth = page.getMediaBox().getWidth();
            float yStart = 780;
            float margin = 50;
            float y = yStart;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // ── Blue Header bar ──
                cs.setNonStrokingColor(new Color(26, 35, 126));
                cs.addRect(0, 810, pageWidth, 32);
                cs.fill();

                // Title text
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.setNonStrokingColor(Color.WHITE);
                cs.newLineAtOffset(margin, 819);
                cs.showText("FICHE ENTREPRISE  -  MindAudit");
                cs.endText();

                // ── Subtitle ──
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 10);
                cs.setNonStrokingColor(new Color(120, 120, 120));
                cs.newLineAtOffset(margin, y);
                cs.showText("Généré le : " + LocalDate.now());
                cs.endText();
                y -= 30;

                // ── Info rows ──
                String[][] rows = {
                    {"Nom", safe(e.getNom())},
                    {"Matricule Fiscal", safe(e.getMatriculeFiscale())},
                    {"Secteur", safe(e.getSecteur())},
                    {"Taille", safe(e.getTaille())},
                    {"Pays", safe(e.getPays())},
                    {"Adresse", safe(e.getAdresse())},
                    {"Email", safe(e.getEmail())},
                    {"Telephone", safe(e.getTelephone())},
                    {"Statut", safe(e.getStatut())},
                    {"Conformite", (e.getComplianceScore() != null ? e.getComplianceScore() : 0) + " %"}
                };

                for (String[] row : rows) {
                    // Label
                    cs.setNonStrokingColor(new Color(240, 244, 255));
                    cs.addRect(margin, y - 4, 160, 20);
                    cs.fill();

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 11);
                    cs.setNonStrokingColor(new Color(44, 62, 80));
                    cs.newLineAtOffset(margin + 5, y + 2);
                    cs.showText(row[0] + " :");
                    cs.endText();

                    // Value
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 11);
                    cs.setNonStrokingColor(new Color(30, 30, 30));
                    cs.newLineAtOffset(margin + 175, y + 2);
                    cs.showText(truncate(row[1], 60));
                    cs.endText();

                    // Separator line
                    cs.setStrokingColor(new Color(220, 220, 220));
                    cs.moveTo(margin, y - 5);
                    cs.lineTo(pageWidth - margin, y - 5);
                    cs.stroke();

                    y -= 25;
                }

                // ── Documents section ──
                y -= 15;
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 13);
                cs.setNonStrokingColor(new Color(26, 35, 126));
                cs.newLineAtOffset(margin, y);
                cs.showText("Documents Associes");
                cs.endText();
                y -= 20;

                if (docs != null && !docs.isEmpty()) {
                    // Table header
                    cs.setNonStrokingColor(new Color(26, 35, 126));
                    cs.addRect(margin, y - 4, pageWidth - 2 * margin, 18);
                    cs.fill();

                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    cs.setNonStrokingColor(Color.WHITE);
                    cs.newLineAtOffset(margin + 5, y + 1);
                    cs.showText("Nom");
                    cs.endText();
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    cs.setNonStrokingColor(Color.WHITE);
                    cs.newLineAtOffset(margin + 200, y + 1);
                    cs.showText("Type");
                    cs.endText();
                    y -= 22;

                    for (Document d : docs) {
                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 10);
                        cs.setNonStrokingColor(new Color(30, 30, 30));
                        cs.newLineAtOffset(margin + 5, y);
                        cs.showText(truncate(safe(d.getNom()), 40));
                        cs.endText();
                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 10);
                        cs.newLineAtOffset(margin + 200, y);
                        cs.showText(truncate(safe(d.getType()), 20));
                        cs.endText();
                        y -= 18;
                        if (y < 60) break;
                    }
                } else {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA, 11);
                    cs.setNonStrokingColor(new Color(120, 120, 120));
                    cs.newLineAtOffset(margin, y);
                    cs.showText("Aucun document associe a cette entreprise.");
                    cs.endText();
                }

                // ── Footer ──
                cs.setNonStrokingColor(new Color(26, 35, 126));
                cs.addRect(0, 0, pageWidth, 22);
                cs.fill();
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 9);
                cs.setNonStrokingColor(Color.WHITE);
                cs.newLineAtOffset(margin, 7);
                cs.showText("MindAudit - Gestion des Entreprises  |  " + LocalDate.now());
                cs.endText();
            }

            doc.save(path);
            System.out.println("PDF genere: " + path);
        }
    }

    public static void exportListeEntreprises(List<Entreprise> list, String path) throws Exception {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth())); // Landscape
            doc.addPage(page);

            float pageWidth = page.getMediaBox().getWidth();
            float margin = 40;
            float y = 540;

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // Header bar
                cs.setNonStrokingColor(new Color(26, 35, 126));
                cs.addRect(0, 560, pageWidth, 32);
                cs.fill();

                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
                cs.setNonStrokingColor(Color.WHITE);
                cs.newLineAtOffset(margin, 570);
                cs.showText("LISTE DES ENTREPRISES  -  MindAudit");
                cs.endText();

                // Column headers
                String[] headers = {"Nom", "Matricule", "Secteur", "Statut", "Conformite"};
                float[] colX = {margin, margin + 160, margin + 290, margin + 460, margin + 560};

                cs.setNonStrokingColor(new Color(44, 62, 80));
                cs.addRect(margin, y - 4, pageWidth - 2 * margin, 18);
                cs.fill();

                for (int i = 0; i < headers.length; i++) {
                    cs.beginText();
                    cs.setFont(PDType1Font.HELVETICA_BOLD, 10);
                    cs.setNonStrokingColor(Color.WHITE);
                    cs.newLineAtOffset(colX[i] + 3, y + 1);
                    cs.showText(headers[i]);
                    cs.endText();
                }
                y -= 22;

                // Data rows
                boolean alt = false;
                for (Entreprise e : list) {
                    if (y < 30) break;
                    if (alt) {
                        cs.setNonStrokingColor(new Color(240, 244, 255));
                        cs.addRect(margin, y - 4, pageWidth - 2 * margin, 18);
                        cs.fill();
                    }

                    String[] vals = {
                        safe(e.getNom()), safe(e.getMatriculeFiscale()),
                        safe(e.getSecteur()), safe(e.getStatut()),
                        (e.getComplianceScore() != null ? e.getComplianceScore() : 0) + " %"
                    };
                    for (int i = 0; i < vals.length; i++) {
                        cs.beginText();
                        cs.setFont(PDType1Font.HELVETICA, 10);
                        cs.setNonStrokingColor(new Color(30, 30, 30));
                        cs.newLineAtOffset(colX[i] + 3, y + 1);
                        cs.showText(truncate(vals[i], i == 2 ? 22 : 18));
                        cs.endText();
                    }
                    y -= 20;
                    alt = !alt;
                }

                // Footer
                cs.setNonStrokingColor(new Color(26, 35, 126));
                cs.addRect(0, 0, pageWidth, 22);
                cs.fill();
                cs.beginText();
                cs.setFont(PDType1Font.HELVETICA, 9);
                cs.setNonStrokingColor(Color.WHITE);
                cs.newLineAtOffset(margin, 7);
                cs.showText("MindAudit - " + LocalDate.now() + "  |  " + list.size() + " entreprise(s)");
                cs.endText();
            }

            doc.save(path);
            System.out.println("PDF liste genere: " + path);
        }
    }

    private static String safe(String s) {
        return s != null ? s : "-";
    }

    private static String truncate(String s, int max) {
        if (s == null) return "-";
        // Remove non-latin1 characters that PDFBox Type1 fonts can't handle
        s = s.replaceAll("[^\\x00-\\xFF]", "");
        return s.length() > max ? s.substring(0, max) + "..." : s;
    }
}
