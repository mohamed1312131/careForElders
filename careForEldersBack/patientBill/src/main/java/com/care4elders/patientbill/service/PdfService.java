package com.care4elders.patientbill.service;

import com.care4elders.patientbill.exception.BillNotFoundException;

import java.io.ByteArrayInputStream;

public interface PdfService {
    
    /**
     * Generate an invoice PDF for a bill
     * 
     * @param billId The ID of the bill
     * @return ByteArrayInputStream containing the PDF data
     * @throws BillNotFoundException If the bill is not found
     * @throws Exception If there's an error generating the PDF
     */
    ByteArrayInputStream generateInvoicePdf(String billId) throws BillNotFoundException, Exception;
    
    /**
     * Generate a payment receipt PDF for a payment
     * 
     * @param paymentId The ID of the payment
     * @return ByteArrayInputStream containing the PDF data
     * @throws Exception If there's an error generating the PDF
     */
    ByteArrayInputStream generatePaymentReceiptPdf(String paymentId) throws Exception;
}
