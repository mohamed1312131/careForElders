package com.care4elders.patientbill.controller;

import com.care4elders.patientbill.service.PdfService;
import com.care4elders.patientbill.exception.BillNotFoundException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;
    
    @GetMapping("/invoice/{billId}")
    public ResponseEntity<InputStreamResource> generateInvoice(@PathVariable String billId) {
        try {
            ByteArrayInputStream pdfStream = pdfService.generateInvoicePdf(billId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=invoice-" + billId + ".pdf");
            
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(pdfStream));
        } catch (BillNotFoundException e) {
            log.error("Bill not found: {}", billId);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error generating PDF for bill: {}", billId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
