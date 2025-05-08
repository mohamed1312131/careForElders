package com.care4elders.patientbill.controller;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.service.PdfService;
import com.care4elders.patientbill.exception.BillNotFoundException;
import com.care4elders.patientbill.exception.ValidationError;
import com.care4elders.patientbill.exception.ValidationErrorResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {
    
    private final BillService billService;
    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        log.info("Fetching all bills");
        List<Bill> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }
    
    // Add endpoint to get bills by patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Bill>> getBillsByPatientId(@PathVariable Long patientId) {
        log.info("Fetching bills for patient id: {}", patientId);
        List<Bill> bills = billService.getBillsByPatientId(patientId);
        return ResponseEntity.ok(bills);
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<Bill> getBillById(@PathVariable String id) {
        try {
            log.info("Fetching bill with id: {}", id);
            Bill bill = billService.getBillById(id);
            return ResponseEntity.ok(bill);
        } catch (BillNotFoundException e) {
            log.error("Bill not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createBill(@Valid @RequestBody Bill bill, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in create bill request");
            return ResponseEntity
                .badRequest()
                .body(createValidationErrorResponse(bindingResult));
        }
        
        // Additional custom validation
        List<ValidationError> customErrors = validateBill(bill);
        if (!customErrors.isEmpty()) {
            log.warn("Custom validation errors in create bill request");
            ValidationErrorResponse errorResponse = new ValidationErrorResponse();
            errorResponse.setErrors(customErrors);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        log.info("Creating new bill for patient: {}", bill.getPatientName());
        Bill createdBill = billService.createBill(bill);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBill);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBill(@PathVariable String id, @Valid @RequestBody Bill bill, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in update bill request");
            return ResponseEntity
                .badRequest()
                .body(createValidationErrorResponse(bindingResult));
        }
        
        // Additional custom validation
        List<ValidationError> customErrors = validateBill(bill);
        if (!customErrors.isEmpty()) {
            log.warn("Custom validation errors in update bill request");
            ValidationErrorResponse errorResponse = new ValidationErrorResponse();
            errorResponse.setErrors(customErrors);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            log.info("Updating bill with id: {}", id);
            Bill updatedBill = billService.updateBill(id, bill);
            return ResponseEntity.ok(updatedBill);
        } catch (BillNotFoundException e) {
            log.error("Bill not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable String id) {
        try {
            log.info("Deleting bill with id: {}", id);
            billService.deleteBill(id);
            return ResponseEntity.noContent().build();
        } catch (BillNotFoundException e) {
            log.error("Bill not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<InputStreamResource> generatePdf(@PathVariable String id) {
        try {
            log.info("Generating PDF for bill with id: {}", id);
            ByteArrayInputStream bis = pdfService.generateInvoicePdf(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=invoice_"+id+".pdf");
            
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(bis));
        } catch (BillNotFoundException e) {
            log.error("Bill not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error generating PDF for bill with id: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    private ValidationErrorResponse createValidationErrorResponse(BindingResult bindingResult) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        
        List<ValidationError> errors = bindingResult.getFieldErrors().stream()
            .map(error -> new ValidationError(
                error.getField(), 
                error.getDefaultMessage()))
            .collect(Collectors.toList());
            
        errorResponse.setErrors(errors);
        return errorResponse;
    }
    
    private List<ValidationError> validateBill(Bill bill) {
        List<ValidationError> errors = new ArrayList<>();
    
        // Validate bill has at least one item
        if (bill.getItems() == null || bill.getItems().isEmpty()) {
            errors.add(new ValidationError("items", "Bill must have at least one item"));
        }
        return errors;
    }
}
