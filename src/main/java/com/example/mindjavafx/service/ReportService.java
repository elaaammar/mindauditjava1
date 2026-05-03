package com.example.mindjavafx.service;

import com.example.mindjavafx.model.Audit;
import com.example.mindjavafx.model.Report;
import com.example.mindjavafx.util.DatabaseConnection;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReportService {

    private static final String REPORTS_DIR = "reports";

    public ReportService() {
        // Create reports directory if it doesn't exist
        File reportsDir = new File(REPORTS_DIR);
        if (!reportsDir.exists()) {
            reportsDir.mkdirs();
        }
    }

    /**
     * Get all reports for a specific user
     */
    public List<Report> getReportsByUserId(int userId) {
        List<Report> reports = new ArrayList<>();
        String query = "SELECT * FROM report WHERE user_id = ? ORDER BY generated_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching reports: " + e.getMessage());
            e.printStackTrace();
        }

        return reports;
    }

    /**
     * Generate a PDF report for an audit
     */
    public Report generatePdfReport(Audit audit) {
        try {
            // Create filename
            String timestamp = System.currentTimeMillis() + "";
            String filename = "rapport_audit_" + audit.getId() + "_" + timestamp + ".pdf";
            String filePath = REPORTS_DIR + File.separator + filename;

            // Create PDF document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            // Title
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("RAPPORT D'AUDIT");
            contentStream.endText();

            // Audit name
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, 720);
            contentStream.showText(audit.getName());
            contentStream.endText();

            // Date
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 700);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            contentStream.showText("Date: " + audit.getAuditDate().format(formatter));
            contentStream.endText();

            // Category
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 680);
            contentStream.showText("Categorie: " + audit.getCategory());
            contentStream.endText();

            // Line separator
            contentStream.moveTo(50, 665);
            contentStream.lineTo(550, 665);
            contentStream.stroke();

            // Scores section
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, 640);
            contentStream.showText("SCORES");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 620);
            contentStream.showText("Score Global: " + audit.getGlobalScore() + "/100");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 600);
            contentStream.showText("Score Securite: " + audit.getSecurityScore() + "/100");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 580);
            contentStream.showText("Score Conformite: " + audit.getComplianceScore() + "/100");
            contentStream.endText();

            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 560);
            contentStream.showText("Score Performance: " + audit.getPerformanceScore() + "/100");
            contentStream.endText();

            // Line separator
            contentStream.moveTo(50, 545);
            contentStream.lineTo(550, 545);
            contentStream.stroke();

            // Findings section
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, 520);
            contentStream.showText("CONSTATATIONS");
            contentStream.endText();

            // Findings text (wrap if too long)
            String findings = audit.getFindings() != null ? audit.getFindings() : "Aucune constatation";
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 11);
            contentStream.newLineAtOffset(50, 500);
            
            // Simple text wrapping
            int maxWidth = 80;
            if (findings.length() > maxWidth) {
                String[] words = findings.split(" ");
                StringBuilder line = new StringBuilder();
                float yPosition = 500;
                
                for (String word : words) {
                    if (line.length() + word.length() + 1 > maxWidth) {
                        contentStream.showText(line.toString());
                        contentStream.newLineAtOffset(0, -15);
                        yPosition -= 15;
                        line = new StringBuilder(word + " ");
                    } else {
                        line.append(word).append(" ");
                    }
                }
                if (line.length() > 0) {
                    contentStream.showText(line.toString());
                }
            } else {
                contentStream.showText(findings);
            }
            contentStream.endText();

            // Footer
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(50, 50);
            contentStream.showText("MindAudit - Systeme de Gestion d'Audits Internes");
            contentStream.endText();

            contentStream.close();

            // Save document
            document.save(filePath);
            document.close();

            // Get file size
            File pdfFile = new File(filePath);
            long fileSize = pdfFile.length();

            // Create Report object
            Report report = new Report(audit.getUserId(), audit.getId(), filename, filePath, fileSize);

            // Save report metadata to database
            if (saveReport(report)) {
                return report;
            }

        } catch (IOException e) {
            System.err.println("Error generating PDF report: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Save report metadata to database
     */
    public boolean saveReport(Report report) {
        String query = "INSERT INTO report (user_id, audit_id, name, file_path, file_size) " +
                      "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, report.getUserId());
            stmt.setInt(2, report.getAuditId());
            stmt.setString(3, report.getName());
            stmt.setString(4, report.getFilePath());
            stmt.setLong(5, report.getFileSize());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    report.setId(generatedKeys.getInt(1));
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error saving report: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get report file
     */
    public File getReportFile(int reportId) {
        String query = "SELECT file_path FROM report WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, reportId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String filePath = rs.getString("file_path");
                File file = new File(filePath);
                if (file.exists()) {
                    return file;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching report file: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Helper method to map ResultSet to Report object
     */
    private Report mapResultSetToReport(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("id"));
        report.setUserId(rs.getInt("user_id"));
        report.setAuditId(rs.getInt("audit_id"));
        report.setName(rs.getString("name"));
        report.setFilePath(rs.getString("file_path"));
        report.setFileSize(rs.getLong("file_size"));

        Timestamp generatedAtTs = rs.getTimestamp("generated_at");
        if (generatedAtTs != null) {
            report.setGeneratedAt(generatedAtTs.toLocalDateTime());
        }

        return report;
    }
}
