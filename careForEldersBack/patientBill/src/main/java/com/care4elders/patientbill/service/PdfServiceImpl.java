// src/main/java/com/care4elders/patientbill/service/PdfServiceImpl.java
package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.ServiceItem;

import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.pdf.PdfPTable;

@Service
public class PdfServiceImpl implements PdfService {

    private final BillService billService;

    public PdfServiceImpl(BillService billService) {
        this.billService = billService;
    }

    @Override
    public ByteArrayInputStream generateInvoicePdf(Long billId) throws Exception {
        Bill bill = billService.getBillById(billId);
        
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            
            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Patient Bill Invoice", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);
            
            // Add invoice details
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            
            document.add(new Paragraph("Invoice Number: " + bill.getId(), headerFont));
            document.add(new Paragraph("Patient Name: " + bill.getPatientName(), normalFont));
            document.add(new Paragraph("Patient ID: " + bill.getPatientId(), normalFont));
            document.add(new Paragraph("Bill Date: " + bill.getBillDate(), normalFont));
            document.add(new Paragraph("Due Date: " + bill.getDueDate(), normalFont));
            document.add(new Paragraph("Status: " + bill.getStatus(), normalFont));
            document.add(Chunk.NEWLINE);
            
            // Add services table
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            
            // Table headers
            table.addCell(new Phrase("Service", headerFont));
            table.addCell(new Phrase("Description", headerFont));
            table.addCell(new Phrase("Rate", headerFont));
            table.addCell(new Phrase("Quantity", headerFont));
            
            // Table rows
            for (ServiceItem service : bill.getServices()) {
                table.addCell(new Phrase(service.getServiceName(), normalFont));
                table.addCell(new Phrase(service.getDescription(), normalFont));
                table.addCell(new Phrase(String.format("$%.2f", service.getRate()), normalFont));
                table.addCell(new Phrase(String.valueOf(service.getQuantity()), normalFont));
            }
            
            document.add(table);
            
            // Add total amount
            Paragraph total = new Paragraph(
                String.format("Total Amount: $%.2f", bill.getTotalAmount()), 
                headerFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            document.add(total);
            
            // Add notes if available
            if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("Notes: " + bill.getNotes(), normalFont));
            }
            
            document.close();
        } catch (DocumentException e) {
            throw new Exception("Error generating PDF", e);
        }
        
        return new ByteArrayInputStream(out.toByteArray());
    }
}