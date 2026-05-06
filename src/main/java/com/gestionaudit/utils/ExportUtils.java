package com.gestionaudit.utils;

import com.gestionaudit.models.Reclamation;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExportUtils {

    public static void exportToPDF(Reclamation r, String filePath) throws IOException {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            Paragraph title = new Paragraph("Details de la reclamation");
            title.setSpacingAfter(12f);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100f);
            table.addCell("ID:");
            table.addCell(String.valueOf(r.getId()));
            table.addCell("Titre:");
            table.addCell(r.getTitre());
            table.addCell("Description:");
            table.addCell(r.getDescription());
            table.addCell("Statut:");
            table.addCell(r.getStatut());
            table.addCell("Client:");
            table.addCell(r.getNom());
            table.addCell("Email:");
            table.addCell(r.getEmail());
            table.addCell("Date:");
            table.addCell(r.getDateCreation().toString());

            document.add(table);
        } catch (Exception e) {
            throw new IOException("Could not generate PDF: " + e.getMessage(), e);
        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }
    }

    public static void exportToExcel(Reclamation r, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Réclamation " + r.getId());

        String[] headers = {"Champ", "Valeur"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        Object[][] data = {
                {"ID", r.getId()},
                {"Titre", r.getTitre()},
                {"Description", r.getDescription()},
                {"Statut", r.getStatut()},
                {"Nom Client", r.getNom()},
                {"Email", r.getEmail()},
                {"Date Création", r.getDateCreation().toString()}
        };

        int rowNum = 1;
        for (Object[] rowData : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(rowData[0].toString());
            row.createCell(1).setCellValue(rowData[1].toString());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}
