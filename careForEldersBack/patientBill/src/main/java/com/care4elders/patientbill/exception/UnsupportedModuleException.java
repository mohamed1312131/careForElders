package com.care4elders.patientbill.exception;

public class UnsupportedModuleException extends RuntimeException {
    
    public UnsupportedModuleException(String message) {
        super(message);
    }
    
    public UnsupportedModuleException(String message, Throwable cause) {
        super(message, cause);
    }
}