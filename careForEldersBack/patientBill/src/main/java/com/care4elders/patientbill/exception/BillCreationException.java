package com.care4elders.patientbill.exception;

public class BillCreationException extends RuntimeException {
    
    public BillCreationException(String message) {
        super(message);
    }
    
    public BillCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}