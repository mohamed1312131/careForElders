package com.care4elders.patientbill.service;

import com.care4elders.patientbill.exception.BillNotFoundException;
import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream generateInvoicePdf(String billId) throws BillNotFoundException, Exception;
    
    // You can add a method for generating payment receipts as well
    default ByteArrayInputStream generatePaymentReceipt(String paymentId) throws Exception {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
