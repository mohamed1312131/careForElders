package com.care4elders.patientbill.service;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillItem;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.exception.BillNotFoundException;
import com.care4elders.patientbill.repository.PaymentRepository;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.borders.SolidBorder;

import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {
    
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(66, 133, 244);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
    
    private final BillService billService;
    private final PaymentRepository paymentRepository;
    
    @Override
    public ByteArrayInputStream generateInvoicePdf(String billId) throws BillNotFoundException, Exception {
        log.info("Generating PDF for bill with id: {}", billId);
        Bill bill = billService.getBillById(billId);
        List<Payment> payments = paymentRepository.findByBillId(billId);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            // Create PDF document
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Add title and logo
            Paragraph title = new Paragraph("INVOICE")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24)
                .setBold();
            document.add(title);
            
            // Add company info
            Paragraph companyInfo = new Paragraph("Care For Elders Health Services")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14);
            document.add(companyInfo);
            
            Paragraph companyAddress = new Paragraph("123 Healthcare Avenue, Medical District\nPhone: (555) 123-4567\nEmail: billing@care4elders.com")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10);
            document.add(companyAddress);
            
            document.add(new Paragraph(" ")); // Empty line
            
            // Create a table for invoice details
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
            infoTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add invoice details
            infoTable.addCell(createCell("Invoice Number:", true));
            infoTable.addCell(createCell(bill.getBillNumber(), false));
            
            infoTable.addCell(createCell("Invoice Date:", true));
            infoTable.addCell(createCell(formatDate(bill.getBillDate()), false));
            
            infoTable.addCell(createCell("Due Date:", true));
            infoTable.addCell(createCell(formatDate(bill.getDueDate()), false));
            
            infoTable.addCell(createCell("Status:", true));
            Cell statusCell = createCell(bill.getStatus(), false);
            if ("PAID".equals(bill.getStatus())) {
                statusCell.setFontColor(ColorConstants.GREEN);
            } else if ("OVERDUE".equals(bill.getStatus())) {
                statusCell.setFontColor(ColorConstants.RED);
            } else if ("PARTIALLY_PAID".equals(bill.getStatus())) {
                statusCell.setFontColor(ColorConstants.BLUE);
            }
            infoTable.addCell(statusCell);
            
            document.add(infoTable);
            document.add(new Paragraph(" ")); // Empty line
            
            // Add patient details
            Paragraph patientTitle = new Paragraph("Patient Information")
                .setFontSize(14)
                .setBold();
            document.add(patientTitle);
            
            Table patientTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}));
            patientTable.setWidth(UnitValue.createPercentValue(100));
            
            patientTable.addCell(createCell("Patient ID:", true));
            patientTable.addCell(createCell(bill.getPatientId().toString(), false));
            
            patientTable.addCell(createCell("Name:", true));
            patientTable.addCell(createCell(bill.getPatientName(), false));
            
            patientTable.addCell(createCell("Email:", true));
            patientTable.addCell(createCell(bill.getPatientEmail(), false));
            
            patientTable.addCell(createCell("Phone:", true));
            patientTable.addCell(createCell(bill.getPatientPhone(), false));
            
            document.add(patientTable);
            document.add(new Paragraph(" ")); // Empty line
            
            // Create table for bill items
            Paragraph itemsTitle = new Paragraph("Services & Items")
                .setFontSize(14)
                .setBold();
            document.add(itemsTitle);
            
            Table itemsTable = new Table(UnitValue.createPercentArray(new float[]{40, 15, 15, 15, 15}));
            itemsTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add table headers
            itemsTable.addHeaderCell(createHeaderCell("Description"));
            itemsTable.addHeaderCell(createHeaderCell("Service Date"));
            itemsTable.addHeaderCell(createHeaderCell("Quantity"));
            itemsTable.addHeaderCell(createHeaderCell("Unit Price"));
            itemsTable.addHeaderCell(createHeaderCell("Amount"));
            
            // Add table rows
            for (BillItem item : bill.getItems()) {
                itemsTable.addCell(createCell(item.getDescription(), false));
                itemsTable.addCell(createCell(formatDate(item.getServiceDate()), false));
                itemsTable.addCell(createCell(String.valueOf(item.getQuantity()), false).setTextAlignment(TextAlignment.RIGHT));
                itemsTable.addCell(createCell(formatCurrency(item.getUnitPrice()), false).setTextAlignment(TextAlignment.RIGHT));
                
                // Calculate amount
                BigDecimal amount = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                itemsTable.addCell(createCell(formatCurrency(amount), false).setTextAlignment(TextAlignment.RIGHT));
            }
            
            document.add(itemsTable);
            document.add(new Paragraph(" ")); // Empty line
            
            // Add summary table
            Table summaryTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            summaryTable.setWidth(UnitValue.createPercentValue(100));
            
            summaryTable.addCell(createCell("Total Amount:", true).setTextAlignment(TextAlignment.RIGHT));
            summaryTable.addCell(createCell(formatCurrency(bill.getTotalAmount()), false).setTextAlignment(TextAlignment.RIGHT));
            
            summaryTable.addCell(createCell("Paid Amount:", true).setTextAlignment(TextAlignment.RIGHT));
            summaryTable.addCell(createCell(formatCurrency(bill.getPaidAmount()), false).setTextAlignment(TextAlignment.RIGHT));
            
            summaryTable.addCell(createCell("Balance Due:", true).setTextAlignment(TextAlignment.RIGHT));
            Cell balanceCell = createCell(formatCurrency(bill.getBalanceAmount()), false).setTextAlignment(TextAlignment.RIGHT);
            if (bill.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
                balanceCell.setFontColor(ColorConstants.GREEN);
            }
            summaryTable.addCell(balanceCell);
            
            document.add(summaryTable);
            document.add(new Paragraph(" ")); // Empty line
            
            // Add payment history if available
            if (!payments.isEmpty()) {
                Paragraph paymentsTitle = new Paragraph("Payment History")
                    .setFontSize(14)
                    .setBold();
                document.add(paymentsTitle);
                
                Table paymentsTable = new Table(UnitValue.createPercentArray(new float[]{20, 15, 20, 25, 20}));
                paymentsTable.setWidth(UnitValue.createPercentValue(100));
                
                paymentsTable.addHeaderCell(createHeaderCell("Date"));
                paymentsTable.addHeaderCell(createHeaderCell("Method"));
                paymentsTable.addHeaderCell(createHeaderCell("Status"));
                paymentsTable.addHeaderCell(createHeaderCell("Transaction ID"));
                paymentsTable.addHeaderCell(createHeaderCell("Amount"));
                
                for (Payment payment : payments) {
                    // Convert LocalDateTime to Date if needed
                    Date paymentDate = payment.getTimestamp() != null 
                        ? Date.from(payment.getTimestamp().atZone(ZoneId.systemDefault()).toInstant()) 
                        : null;
                    
                    paymentsTable.addCell(createCell(formatDateTime(paymentDate), false));
                    paymentsTable.addCell(createCell(payment.getPaymentMethod(), false));
                    
                    Cell paymentStatusCell = createCell(payment.getStatus(), false);
                    if ("COMPLETED".equals(payment.getStatus())) {
                        paymentStatusCell.setFontColor(ColorConstants.GREEN);
                    } else if ("FAILED".equals(payment.getStatus())) {
                        paymentStatusCell.setFontColor(ColorConstants.RED);
                    } else if ("PENDING".equals(payment.getStatus())) {
                        paymentStatusCell.setFontColor(ColorConstants.BLUE);
                    }
                    paymentsTable.addCell(paymentStatusCell);
                    
                    paymentsTable.addCell(createCell(payment.getId(), false));
                    paymentsTable.addCell(createCell(formatCurrency(BigDecimal.valueOf(payment.getAmount())), false)
                        .setTextAlignment(TextAlignment.RIGHT));
                }
                
                document.add(paymentsTable);
                document.add(new Paragraph(" ")); // Empty line
            }
            
            // Add payment options section
            Paragraph paymentOptionsTitle = new Paragraph("Payment Options")
                .setFontSize(14)
                .setBold();
            document.add(paymentOptionsTitle);
            
            Table paymentOptionsTable = new Table(UnitValue.createPercentArray(new float[]{100}));
            paymentOptionsTable.setWidth(UnitValue.createPercentValue(100));
            
            Cell cashOptionCell = new Cell()
                .add(new Paragraph("Cash Payment").setBold())
                .add(new Paragraph("Pay in person at our billing counter during business hours (8:00 AM - 5:00 PM, Monday to Friday)."));
            paymentOptionsTable.addCell(cashOptionCell);
            
            Cell onlineOptionCell = new Cell()
                .add(new Paragraph("Online Payment").setBold())
                .add(new Paragraph("Visit our patient portal at https://care4elders.com/pay and enter your invoice number to make a secure online payment."));
            paymentOptionsTable.addCell(onlineOptionCell);
            
            document.add(paymentOptionsTable);
            document.add(new Paragraph(" ")); // Empty line
            
            // Add notes if available
            if (bill.getNotes() != null && !bill.getNotes().isEmpty()) {
                Paragraph notesTitle = new Paragraph("Notes")
                    .setFontSize(14)
                    .setBold();
                document.add(notesTitle);
                
                document.add(new Paragraph(bill.getNotes()));
                document.add(new Paragraph(" ")); // Empty line
            }
            
            // Add payment instructions
            Paragraph paymentInstructions = new Paragraph("Payment Instructions")
                .setFontSize(12)
                .setBold();
            document.add(paymentInstructions);
            
            document.add(new Paragraph("1. Please include your invoice number with your payment.")
                .setFontSize(10));
            document.add(new Paragraph("2. Make checks payable to 'Care For Elders Health Services'.")
                .setFontSize(10));
            document.add(new Paragraph("3. For questions regarding your bill, please contact our billing department at (555) 123-4567.")
                .setFontSize(10));
            
            document.add(new Paragraph(" ")); // Empty line
            
            // Add footer
            Paragraph footer = new Paragraph("Thank you for choosing Care For Elders Health Services")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10)
                .setItalic();
            document.add(footer);
            
            // Close document
            document.close();
            
            log.info("PDF generated successfully for bill with id: {}", billId);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            log.error("Error generating PDF for bill with id: {}", billId, e);
            throw e;
        }
    }
    
    /**
     * Generate a payment receipt PDF for a specific payment
     */
    public ByteArrayInputStream generatePaymentReceiptPdf(String paymentId) throws Exception {
        log.info("Generating payment receipt PDF for payment with id: {}", paymentId);
        
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));
        
        // Get bill information if available
        Optional<Bill> billOpt = Optional.empty();
        try {
            billOpt = Optional.ofNullable(billService.getBillById(payment.getBillId()));
        } catch (Exception e) {
            log.warn("Could not find bill for payment: {}", paymentId);
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            // Create PDF document
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            
            // Add title
            Paragraph title = new Paragraph("PAYMENT RECEIPT")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(24)
                .setBold();
            document.add(title);
            
            // Add receipt number
            Paragraph receiptNumber = new Paragraph("Receipt #: RCP-" + payment.getId().substring(0, 8).toUpperCase())
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10);
            document.add(receiptNumber);
            
            document.add(new Paragraph(" ")); // Empty line
            
            // Add company info
            Paragraph companyInfo = new Paragraph("Care For Elders Health Services")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14);
            document.add(companyInfo);
            
            Paragraph companyAddress = new Paragraph("123 Healthcare Avenue, Medical District\nPhone: (555) 123-4567\nEmail: billing@care4elders.com")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(10);
            document.add(companyAddress);
            
            document.add(new Paragraph(" ")); // Empty line
            
            // Create a table for payment details
            Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}));
            infoTable.setWidth(UnitValue.createPercentValue(100));
            
            // Left column - Patient info
            Cell patientCell = new Cell()
                .setBorder(null)
                .add(new Paragraph("Patient Information:").setBold());
            
            if (billOpt.isPresent()) {
                Bill bill = billOpt.get();
                patientCell.add(new Paragraph("Name: " + bill.getPatientName()));
                patientCell.add(new Paragraph("ID: " + bill.getPatientId()));
            } else {
                patientCell.add(new Paragraph("Bill ID: " + payment.getBillId()));
            }
            
            // Right column - Payment info
            Cell paymentCell = new Cell()
                .setBorder(null)
                .add(new Paragraph("Payment Information:").setBold())
                .add(new Paragraph("Payment ID: " + payment.getId()))
                .add(new Paragraph("Date: " + formatLocalDateTime(payment.getTimestamp())))
                .add(new Paragraph("Method: " + formatPaymentMethod(payment.getPaymentMethod())))
                .add(new Paragraph("Status: " + formatPaymentStatus(payment.getStatus())));
            
            infoTable.addCell(patientCell);
            infoTable.addCell(paymentCell);
            
            document.add(infoTable);
            document.add(new Paragraph(" ")); // Empty line
            
            // Add payment details table
            Table paymentDetailsTable = new Table(UnitValue.createPercentArray(new float[]{70, 30}));
            paymentDetailsTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add header
            Cell descriptionHeader = new Cell()
                .setBackgroundColor(HEADER_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .add(new Paragraph("Description").setBold())
                .setTextAlignment(TextAlignment.CENTER);
            
            Cell amountHeader = new Cell()
                .setBackgroundColor(HEADER_COLOR)
                .setFontColor(ColorConstants.WHITE)
                .add(new Paragraph("Amount").setBold())
                .setTextAlignment(TextAlignment.CENTER);
            
            paymentDetailsTable.addHeaderCell(descriptionHeader);
            paymentDetailsTable.addHeaderCell(amountHeader);
            
            // Add payment details
            String description = billOpt.isPresent() 
                ? "Payment for Invoice #" + billOpt.get().getBillNumber()
                : "Payment for Medical Services";
            
            Cell descriptionCell = new Cell()
                .add(new Paragraph(description));
            
            Cell amountCell = new Cell()
                .add(new Paragraph(formatCurrency(BigDecimal.valueOf(payment.getAmount()))))
                .setTextAlignment(TextAlignment.RIGHT);
            
            paymentDetailsTable.addCell(descriptionCell);
            paymentDetailsTable.addCell(amountCell);
            
            // Add total row
            Cell totalLabelCell = new Cell()
                .add(new Paragraph("Total").setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            
            Cell totalAmountCell = new Cell()
                .add(new Paragraph(formatCurrency(BigDecimal.valueOf(payment.getAmount()))).setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setBorder(new SolidBorder(ColorConstants.BLACK, 1));
            
            paymentDetailsTable.addCell(totalLabelCell);
            paymentDetailsTable.addCell(totalAmountCell);
            
            document.add(paymentDetailsTable);
            document.add(new Paragraph(" ")); // Empty line
            
            // Add payment status message
            Paragraph statusMessage = new Paragraph();
            if ("COMPLETED".equals(payment.getStatus())) {
                statusMessage = new Paragraph("This payment has been successfully processed. Thank you!")
                    .setFontColor(ColorConstants.GREEN)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setBold();
            } else if ("PENDING".equals(payment.getStatus())) {
                statusMessage = new Paragraph("This payment is being processed. Please check back later.")
                    .setFontColor(ColorConstants.BLUE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setBold();
            } else {
                statusMessage = new Paragraph("This payment was not successful. Please contact billing department.")
                    .setFontColor(ColorConstants.RED)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12)
                    .setBold();
            }
            document.add(statusMessage);
            document.add(new Paragraph(" ")); // Empty line
            
            // Add footer
            Paragraph footer = new Paragraph("Thank you for your payment!")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(12)
                .setBold();
            document.add(footer);
            
            document.add(new Paragraph("This is an electronically generated receipt and does not require a signature.")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setItalic());
            
            // Close document
            document.close();
            
            log.info("Payment receipt PDF generated successfully for payment with id: {}", paymentId);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            log.error("Error generating payment receipt PDF for payment with id: {}", paymentId, e);
            throw e;
        }
    }
    
    private Cell createHeaderCell(String text) {
        Cell cell = new Cell().add(new Paragraph(text).setBold());
        cell.setBackgroundColor(ColorConstants.LIGHT_GRAY);
        cell.setBorder(new SolidBorder(ColorConstants.BLACK, 1));
        cell.setPadding(5);
        return cell;
    }
    
    private Cell createCell(String text, boolean isBold) {
        Paragraph p = new Paragraph(text);
        if (isBold) {
            p.setBold();
        }
        Cell cell = new Cell().add(p);
        cell.setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));
        cell.setPadding(5);
        return cell;
    }
    
    private String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        return formatter.format(date);
    }
    
    private String formatDateTime(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        return formatter.format(date);
    }
    
    private String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_FORMATTER);
    }
    
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "$0.00";
        return String.format("$%.2f", amount);
    }
    
    private String formatPaymentMethod(String paymentMethod) {
        if (paymentMethod == null) return "Unknown";
        return paymentMethod.substring(0, 1).toUpperCase() + paymentMethod.substring(1).toLowerCase();
    }
    
    private String formatPaymentStatus(String status) {
        if (status == null) return "Unknown";
        return status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
    }
}
