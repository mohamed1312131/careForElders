package com.care4elders.patientbill.controller;

import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillItem;
import com.care4elders.patientbill.model.BillItemType;
import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.service.PdfService;
import com.care4elders.patientbill.dto.AutoBillRequest;
import com.care4elders.patientbill.exception.BillNotFoundException;
import com.care4elders.patientbill.exception.ValidationError;
import com.care4elders.patientbill.exception.ValidationErrorResponse;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/bills")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class BillController {
    
    private final BillService billService;
    private final PdfService pdfService;

    @GetMapping
    public ResponseEntity<List<Bill>> getAllBills() {
        log.info("Fetching all bills");
        List<Bill> bills = billService.getAllBills();
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Bill>> getBillsByPatientId(@PathVariable String patientId) {
        log.info("Fetching bills for patient id: {}", patientId);
        List<Bill> bills = billService.getBillsByPatientId(patientId);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/service-type/{serviceType}")
    public ResponseEntity<List<Bill>> getBillsByServiceType(@PathVariable BillItem.ServiceType serviceType) {
        log.info("Fetching bills for service type: {}", serviceType);
        List<Bill> bills = billService.getBillsByServiceType(serviceType);
        return ResponseEntity.ok(bills);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Bill>> getBillsByStatus(@PathVariable String status) {
        log.info("Fetching bills with status: {}", status);
        List<Bill> bills = billService.getBillsByStatus(status);
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
        
        // Process bill items and calculate amounts
        processBillItems(bill);
        
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
        
        // Process bill items and calculate amounts
        processBillItems(bill);
        
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

    // Bill Item specific endpoints
    @PostMapping("/{billId}/items")
    public ResponseEntity<?> addBillItem(@PathVariable String billId, @Valid @RequestBody BillItem billItem, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in add bill item request");
            return ResponseEntity
                .badRequest()
                .body(createValidationErrorResponse(bindingResult));
        }
        
        try {
            log.info("Adding item to bill with id: {}", billId);
            billItem.setBillId(billId);
            billItem.setCreatedAt(LocalDateTime.now());
            billItem.setUpdatedAt(LocalDateTime.now());
            billItem.calculateAmount();
            
            Bill updatedBill = billService.addBillItem(billId, billItem);
            return ResponseEntity.ok(updatedBill);
        } catch (BillNotFoundException e) {
            log.error("Bill not found with id: {}", billId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{billId}/items/{itemId}")
    public ResponseEntity<?> updateBillItem(@PathVariable String billId, @PathVariable String itemId, 
                                          @Valid @RequestBody BillItem billItem, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Validation errors in update bill item request");
            return ResponseEntity
                .badRequest()
                .body(createValidationErrorResponse(bindingResult));
        }
        
        try {
            log.info("Updating item {} in bill {}", itemId, billId);
            billItem.setId(itemId);
            billItem.setBillId(billId);
            billItem.setUpdatedAt(LocalDateTime.now());
            billItem.calculateAmount();
            
            Bill updatedBill = billService.updateBillItem(billId, itemId, billItem);
            return ResponseEntity.ok(updatedBill);
        } catch (BillNotFoundException e) {
            log.error("Bill or item not found - Bill ID: {}, Item ID: {}", billId, itemId);
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{billId}/items/{itemId}")
    public ResponseEntity<Bill> removeBillItem(@PathVariable String billId, @PathVariable String itemId) {
        try {
            log.info("Removing item {} from bill {}", itemId, billId);
            Bill updatedBill = billService.removeBillItem(billId, itemId);
            return ResponseEntity.ok(updatedBill);
        } catch (BillNotFoundException e) {
            log.error("Bill or item not found - Bill ID: {}, Item ID: {}", billId, itemId);
            return ResponseEntity.notFound().build();
        }
    }

    // Get available bill item types for a service type
    @GetMapping("/item-types/{serviceType}")
    public ResponseEntity<List<BillItemType>> getItemTypesForServiceType(@PathVariable BillItem.ServiceType serviceType) {
        log.info("Fetching item types for service type: {}", serviceType);
        List<BillItemType> itemTypes = BillItemType.getItemsForServiceType(serviceType);
        return ResponseEntity.ok(itemTypes);
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
    
    // Payment related endpoints
    @PostMapping("/{id}/payment")
    public ResponseEntity<?> recordPayment(@PathVariable String id, @RequestBody PaymentRequest paymentRequest) {
        try {
            log.info("Recording payment for bill with id: {}", id);
            Bill updatedBill = billService.recordPayment(id, paymentRequest.getAmount(), paymentRequest.getPaymentMethod());
            return ResponseEntity.ok(updatedBill);
        } catch (BillNotFoundException e) {
            log.error("Bill not found with id: {}", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid payment amount for bill with id: {}", id);
            return ResponseEntity.badRequest().body(new ValidationErrorResponse());
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
        
        // Validate bill items
        if (bill.getItems() != null) {
            for (int i = 0; i < bill.getItems().size(); i++) {
                BillItem item = bill.getItems().get(i);
                String fieldPrefix = "items[" + i + "]";
                
                if (item.getQuantity() != null && item.getQuantity() <= 0) {
                    errors.add(new ValidationError(fieldPrefix + ".quantity", "Quantity must be greater than 0"));
                }
                
                if (item.getUnitPrice() != null && item.getUnitPrice().compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add(new ValidationError(fieldPrefix + ".unitPrice", "Unit price must be greater than 0"));
                }
            }
        }
        
        // Validate dates
        if (bill.getBillDate() != null && bill.getDueDate() != null) {
            if (bill.getDueDate().before(bill.getBillDate())) {
                errors.add(new ValidationError("dueDate", "Due date cannot be before bill date"));
            }
        }
        
        return errors;
    }
    
    private void processBillItems(Bill bill) {
        if (bill.getItems() != null) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            for (BillItem item : bill.getItems()) {
                // Set timestamps
                if (item.getCreatedAt() == null) {
                    item.setCreatedAt(LocalDateTime.now());
                }
                item.setUpdatedAt(LocalDateTime.now());
                
                // Calculate item amount
                item.calculateAmount();
                
                // Add to total
                totalAmount = totalAmount.add(item.getAmount());
                
                // Set bill reference
                item.setBillId(bill.getId());
            }
            
            // Update bill totals
            bill.setTotalAmount(totalAmount);
            
            // Calculate balance amount
            BigDecimal paidAmount = bill.getPaidAmount() != null ? bill.getPaidAmount() : BigDecimal.ZERO;
            bill.setBalanceAmount(totalAmount.subtract(paidAmount));
        }
    }
    
    // Inner class for payment requests
    public static class PaymentRequest {
        private BigDecimal amount;
        private String paymentMethod;
        
        // Getters and setters
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
    //Bill automatically 
    @PostMapping("/generate-auto")
    public ResponseEntity<?> generateAutomaticBill(@RequestBody AutoBillRequest request) {
        try {
            log.info("Generating automatic bill for patient: {} with service type: {}", 
                    request.getPatientId(), request.getServiceType());
            
            // Validate required fields
            List<ValidationError> errors = new ArrayList<>();
            if (request.getPatientId() == null || request.getPatientId().trim().isEmpty()) {
                errors.add(new ValidationError("patientId", "Patient ID is required"));
            }
            
            if (request.getServiceType() == null) {
                errors.add(new ValidationError("serviceType", "Service type is required"));
            }
            
            if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                errors.add(new ValidationError("totalAmount", "Total amount must be greater than 0"));
            }
            
            if (!errors.isEmpty()) {
                ValidationErrorResponse errorResponse = new ValidationErrorResponse();
                errorResponse.setErrors(errors);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Create new bill with automatic values
            Bill bill = new Bill();
            bill.setPatientId(request.getPatientId());
            bill.setPatientName(request.getPatientName() != null ? request.getPatientName() : "Patient " + request.getPatientId());
            bill.setBillDate(new java.util.Date()); // Set current date
            bill.setStatus("PENDING");
            bill.setTotalAmount(request.getTotalAmount());
            bill.setPaidAmount(BigDecimal.ZERO);
            bill.setBalanceAmount(request.getTotalAmount());
            
            // Set due date (30 days from bill date by default)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(bill.getBillDate());
            calendar.add(Calendar.DAY_OF_MONTH, 30);
            bill.setDueDate(calendar.getTime());
            
            // Create a single bill item for the service
            BillItem billItem = new BillItem();
            billItem.setServiceType(request.getServiceType());
            billItem.setDescription(getServiceDescription(request.getServiceType()));
            billItem.setQuantity(1);
            billItem.setUnitPrice(request.getTotalAmount());
            billItem.setAmount(request.getTotalAmount());
            billItem.setCreatedAt(LocalDateTime.now());
            billItem.setUpdatedAt(LocalDateTime.now());
            billItem.setServiceDate(java.time.LocalDate.now()); // Set service date to today
            billItem.setUnit("Session"); // Set a default unit
            
            // Add item to bill
            List<BillItem> items = new ArrayList<>();
            items.add(billItem);
            bill.setItems(items);
            
            // Create the bill
            Bill createdBill = billService.createBill(bill);
            
            log.info("Successfully generated automatic bill with id: {}", createdBill.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBill);
            
        } catch (Exception e) {
            log.error("Error generating automatic bill for patient: {}", request.getPatientId(), e);
            return ResponseEntity.internalServerError()
                .body(new ValidationErrorResponse());
        }
    }
private String getServiceDescription(BillItem.ServiceType serviceType) {
    switch (serviceType) {
        case PARA_MEDICAL_SERVICES:
            return "Professional Nursing Care Services";
        case DOCTOR_CARE:
            return "Medical Consultation Services";
        case SUBSCRIPTION:
            return "Subscription to the platforms";
        default:
            return "Healthcare Services";
    }
}

}