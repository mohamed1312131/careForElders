package com.care4elders.patientbill.service;

import com.care4elders.patientbill.exception.BillNotFoundException;
import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream generateInvoicePdf(String billId) throws BillNotFoundException, Exception;
}
