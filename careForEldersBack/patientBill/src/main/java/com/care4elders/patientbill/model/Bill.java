// src/main/java/com/care4elders/patientbill/model/Bill.java
package com.care4elders.patientbill.model;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Data
@Document(collection = "bills")
public class Bill {
    public static final String SEQUENCE_NAME = "bill_sequence";
    
    @Id
    private Long id;  // Changed from String to Long
    private Long patientId;
    private String patientName;
    private LocalDate billDate;
    private LocalDate dueDate;
    private List<ServiceItem> services;
    private double totalAmount;
    private BillStatus status;
    private String notes;
}