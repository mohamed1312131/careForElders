package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillItem;
import com.care4elders.patientbill.exception.BillNotFoundException;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.property.TextAlignment;
//import com.itextpdf.layout.property.UnitValue;

import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {
    
    private final BillService billService;
    
    @Override
    public ByteArrayInputStream generateInvoicePdf(String billId) throws BillNotFoundException, Exception {
        log.info("Generating PDF for bill with id: {}", billId);
        Bill bill = billService.getBillById(billId);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            // Create PDF document
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Add title
            document.add(new Paragraph("INVOICE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(20));
            
            // Add invoice details
            document.add(new Paragraph("Invoice #: " + bill.getId()));
            document.add(new Paragraph("Date: " + formatDate(bill.getBillDate())));
            document.add(new Paragraph("Due Date: " + formatDate(bill.getDueDate())));
            document.add(new Paragraph("Status: " + bill.getStatus()));
            document.add(new Paragraph(" ")); // Empty line
            
            // Add patient details
            document.add(new Paragraph("Patient Information:"));
            document.add(new Paragraph("ID: " + bill.getPatientId()));
            document.add(new Paragraph("Name: " + bill.getPatientName()));
            document.add(new Paragraph(" ")); // Empty line
            
            // Create table for bill items
            Table table = new Table(UnitValue.createPercentArray(new float[]{5, 2, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));
            
            // Add table headers
            table.addHeaderCell(new Cell().add(new Paragraph("Description")));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantity")));
            table.addHeaderCell(new Cell().add(new Paragraph("Unit Price")));
            table.addHeaderCell(new Cell().add(new Paragraph("Amount")));
            
            // Add table rows
            for (BillItem item : bill.getItems()) {
                table.addCell(new Cell().add(new Paragraph(item.getDescription())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
                table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", item.getUnitPrice()))));
                table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", item.getAmount()))));
            }
            
            // Add total row
            table.addCell(new Cell(1, 3).add(new Paragraph("Total")));
            table.addCell(new Cell().add(new Paragraph(String.format("$%.2f", bill.getTotalAmount()))));
            
            document.add(table);
            
            // Add description if available
            if (bill.getDescription() != null && !bill.getDescription().isEmpty()) {
                document.add(new Paragraph(" ")); // Empty line
                document.add(new Paragraph("Additional Information:"));
                document.add(new Paragraph(bill.getDescription()));
            }
            
            // Close document
            document.close();
            
            log.info("PDF generated successfully for bill with id: {}", billId);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            log.error("Error generating PDF for bill with id: {}", billId, e);
            throw e;
        }
    }
    
    private String formatDate(Date date) {
        if (date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }
}
