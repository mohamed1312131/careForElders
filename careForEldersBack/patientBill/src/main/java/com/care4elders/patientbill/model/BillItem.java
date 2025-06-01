package com.care4elders.patientbill.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Document(collection = "bill_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillItem {

    @Id
    private String id;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType; // DOCTOR_CARE, PARA_MEDICAL_SERVICES, SUBSCRIPTION

    @NotBlank(message = "Description is required")
    private String description;



    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be positive")
    private BigDecimal unitPrice;

    @NotNull(message = "Amount is required")
    private BigDecimal amount; // Calculated: quantity * unitPrice

    @NotBlank(message = "Unit is required")
    private String unit; // Session, Visit, Week, Month, etc.

    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    // Reference to the bill (stored as bill ID since we're using MongoDB)
    private String billId;

    // Custom constructor for easy creation with service type
    public BillItem(ServiceType serviceType, String description, String serviceCode, 
                   String category, Integer quantity, BigDecimal unitPrice, String unit) {
        this.serviceType = serviceType;
        this.description = description;
       // this.serviceCode = serviceCode;
       // this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.unit = unit;
        this.serviceDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        calculateAmount();
    }

    // Recalculate amount when quantity or unit price changes
    public void calculateAmount() {
        if (this.quantity != null && this.unitPrice != null) {
            this.amount = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        } else {
            this.amount = BigDecimal.ZERO;
        }
        this.updatedAt = LocalDateTime.now();
    }

    // Setters that automatically recalculate amount
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateAmount();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        calculateAmount();
    }
   
    // Enum for service types
    public enum ServiceType {
        DOCTOR_CARE("Doctor Care"),
        PARA_MEDICAL_SERVICES("Para Medical Services"),
        SUBSCRIPTION("Subscription");

        private final String displayName;

        ServiceType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}