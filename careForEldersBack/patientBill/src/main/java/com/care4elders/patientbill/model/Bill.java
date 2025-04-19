// src/main/java/com/care4elders/patientbill/model/Bill.java
package com.care4elders.patientbill.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.annotation.*;
import lombok.Data;

@Data
public class Bill {
    @Id
    private String id;
    private Long patientId;
    private String patientName;
    private LocalDate billDate;
    private LocalDate dueDate;
    private List<ServiceItem> services;
    private double totalAmount;
    private BillStatus status;
    private String notes;

 
}