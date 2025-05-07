package com.care4elders.patientbill.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "bills")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    
    @Id
    private String id;
    
    @NotBlank(message = "Bill number is required")
    private String billNumber;
    
    @NotNull(message = "Patient ID is required")
    private Integer patientId;
    
    @NotBlank(message = "Patient name is required")
    private String patientName;
    
    private String patientEmail;
    
    private String patientPhone;
    
    @NotNull(message = "Bill date is required")
    private Date billDate;
    
    @NotNull(message = "Due date is required")
    private Date dueDate;
    
    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;
    
    private BigDecimal paidAmount = BigDecimal.ZERO;
    
    private BigDecimal balanceAmount;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    @NotNull(message = "Bill must have at least one item")
    @Size(min = 1, message = "Bill must have at least one item")
    private List<BillItem> items;
    
    private String notes;
}
