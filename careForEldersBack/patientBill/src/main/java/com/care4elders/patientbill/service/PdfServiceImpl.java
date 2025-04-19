package com.care4elders.patientbill.service;

import com.care4elders.patientbill.exception.BillNotFoundException;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.ServiceItem;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.HorizontalAlignment;

@Service
public class PdfServiceImpl implements PdfService {

    private final BillService billService;

    public PdfServiceImpl(BillService billService) {
        this.billService = billService;
    }

    @Override
    public ByteArrayInputStream generateInvoicePdf(Long billId) throws BillNotFoundException {
        Bill bill = billService.getBillById(billId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Font setup
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            
            // Add title
            Paragraph title = new Paragraph("Patient Bill Invoice")
                .setFont(boldFont)
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
            document.add(title);
            
            // Add invoice details
            document.add(new Paragraph("Invoice Number: " + bill.getId())
                .setFont(boldFont)
                .setFontSize(12));
            document.add(new Paragraph("Patient Name: " + bill.getPatientName())
                .setFont(normalFont)
                .setFontSize(12));
            document.add(new Paragraph("Patient ID: " + bill.getPatientId())
                .setFont(normalFont)
                .setFontSize(12));
            document.add(new Paragraph("Bill Date: " + bill.getBillDate())
                .setFont(normalFont)
                .setFontSize(12));
            document.add(new Paragraph("Due Date: " + bill.getDueDate())
                .setFont(normalFont)
                .setFontSize(12));
            document.add(new Paragraph("Status: " + bill.getStatus())
                .setFont(normalFont)
                .setFontSize(12));
            document.add(new Paragraph("\n"));
            
            // Add services table
            float[] columnWidths = {2, 3, 1, 1};
            Table table = new Table(columnWidths);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);
            table.setMarginTop(10);
            table.setMarginBottom(10);
            
            // Table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Service").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Description").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Rate").setFont(boldFont)));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity").setFont(boldFont)));
            
            // Table rows
            for (ServiceItem service : bill.getServices()) {
                table.addCell(new Cell().add(new Paragraph(service.getServiceName()).setFont(normalFont)));
                table.addCell(new Cell().add(new Paragraph(service.getDescription()).setFont(normalFont)));
                table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", service.getRate())).setFont(normalFont)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(service.getQuantity())).setFont(normalFont)));
            }
            
            document.add(table);
            
            // Add total amount
            Paragraph total = new Paragraph(
                String.format("Total Amount: $%.2f", bill.getTotalAmount()))
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT);
            document.add(total);
            
            // Add notes if available
            if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
                document.add(new Paragraph("\n"));
                document.add(new Paragraph("Notes: " + bill.getNotes()).setFont(normalFont));
            }
            
            document.close();
        } catch (Exception e) {
            throw new BillNotFoundException("Error generating PDF for bill ID: " + billId);
        }
        
        return new ByteArrayInputStream(out.toByteArray());
    }
}