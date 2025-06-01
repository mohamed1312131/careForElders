package com.care4elders.blogforum.util;

import java.util.List;

import org.springframework.validation.BindingResult;

import com.care4elders.blogforum.exception.ValidationError;
import com.care4elders.blogforum.exception.ValidationErrorResponse;

public class ValidationUtils {
    
    public static ValidationErrorResponse createValidationErrorResponse(BindingResult bindingResult) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        
        List<ValidationError> errors = bindingResult.getFieldErrors().stream()
                .map(error -> new ValidationError(
                        error.getField(), 
                        error.getDefaultMessage()))
                .toList();
                
        errorResponse.setErrors(errors);
        return errorResponse;
    }
}