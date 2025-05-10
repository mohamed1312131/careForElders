package com.care4elders.patientbill.exception;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationErrorResponse {
    private List<ValidationError> errors = new ArrayList<>();
    
    public void addError(ValidationError error) {
        this.errors.add(error);
    }
}
