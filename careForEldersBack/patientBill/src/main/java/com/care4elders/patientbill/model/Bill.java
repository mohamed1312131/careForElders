package com.care4elders.patientbill.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.*;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "bills")
public class Bill {
    
    @Id
    private String id;
    
    @NotNull(message = "Patient ID is required")
    @Positive(message = "Patient ID must be a positive number")
    private Long patientId;
    
    @NotBlank(message = "Patient name is required")
    @Size(min = 3, max = 100, message = "Patient name must be between 3 and 100 characters")
    private String patientName;
    
    @NotNull(message = "Bill date is required")
    @PastOrPresent(message = "Bill date cannot be in the future")
    private Date billDate;
    
    @NotNull(message = "Due date is required")
    private Date dueDate;
    
    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    private Double totalAmount;
    
    @NotNull(message = "Status is required")
    @Pattern(regexp = "PAID|UNPAID|OVERDUE", message = "Status must be PAID, UNPAID, or OVERDUE")
    private String status;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    private List<BillItem> items;
}
