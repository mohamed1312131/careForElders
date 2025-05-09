package com.care4elders.patientbill.controller;

import com.care4elders.patientbill.service.PdfServiceImpl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentReceiptController {

    private final PdfServiceImpl pdfService;
    
    @GetMapping("/{paymentId}/receipt")
    public ResponseEntity<InputStreamResource> generatePaymentReceipt(@PathVariable String paymentId) {
        try {
            ByteArrayInputStream pdfStream = pdfService.generatePaymentReceiptPdf(paymentId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=payment-receipt-" + paymentId + ".pdf");
            
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));
        } catch (Exception e) {
            log.error("Error generating payment receipt PDF: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
