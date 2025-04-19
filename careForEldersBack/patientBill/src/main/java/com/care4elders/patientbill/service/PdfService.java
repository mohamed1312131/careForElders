// src/main/java/com/care4elders/patientbill/service/PdfService.java
package com.care4elders.patientbill.service;

import java.io.ByteArrayInputStream;

public interface PdfService {
    ByteArrayInputStream generateInvoicePdf(String billId) throws Exception;
}