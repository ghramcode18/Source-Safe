package Geeks.Source.Safe.service;

import Geeks.Source.Safe.Entity.FileLog;
import Geeks.Source.Safe.repo.FileLogRepository;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Service
public class ExportService {

    private final FileLogRepository fileLogRepository;

    public ExportService(FileLogRepository fileLogRepository) {
        this.fileLogRepository = fileLogRepository;
    }

    // Export as CSV
    public byte[] exportTableAsCSV() throws IOException {
        List<FileLog> data = fileLogRepository.findAll();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT);

        // Write header
        csvPrinter.printRecord("ID", "File Name", "User Name", "Action", "Created At", "Updated At");

        // Write data
        for (FileLog record : data) {
            csvPrinter.printRecord(
                    record.getId(),
                    record.getFile().getFileName(),    // Assuming File entity has a "name" field
                    record.getUser().getUserName(), // Assuming User entity has a "username" field
                    record.getAction(),
                    record.getCreatedAt(),
                    record.getUpdatedAt()
            );
        }

        csvPrinter.flush();
        csvPrinter.close();

        return outputStream.toByteArray();
    }

    // Export as PDF
    public byte[] exportTableAsPDF() throws IOException {
        List<FileLog> data = fileLogRepository.findAll();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Use iText for PDF creation
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);  // Correct instantiation of Document

        // Add Title
        document.add(new com.itextpdf.layout.element.Paragraph("File Log Export").setBold().setFontSize(16));

        // Create a table with 6 columns
        Table table = new Table(6); // Specify the number of columns directly, no need for float[] array.

// Add header cells
        table.addHeaderCell(new Cell().add(new com.itextpdf.layout.element.Paragraph("ID")).setBold());
        table.addHeaderCell(new Cell().add(new com.itextpdf.layout.element.Paragraph("File Name")).setBold());
        table.addHeaderCell(new Cell().add(new com.itextpdf.layout.element.Paragraph("User Name")).setBold());
        table.addHeaderCell(new Cell().add(new com.itextpdf.layout.element.Paragraph("Action")).setBold());
        table.addHeaderCell(new Cell().add(new com.itextpdf.layout.element.Paragraph("Created At")).setBold());
        table.addHeaderCell(new Cell().add(new com.itextpdf.layout.element.Paragraph("Updated At")).setBold());

        // Populate table rows
        for (FileLog record : data) {
            table.addCell(record.getId().toString());
            table.addCell(record.getFile().getFileName()); // Assuming File entity has a "fileName" field
            table.addCell(record.getUser().getUserName()); // Assuming User entity has a "userName" field
            table.addCell(record.getAction());
            table.addCell(record.getCreatedAt().toString());
            table.addCell(record.getUpdatedAt().toString());
        }

        // Add the table to the document
        document.add(table);

        // Close the document and return the PDF byte array
        document.close();

        return outputStream.toByteArray();
    }
}
