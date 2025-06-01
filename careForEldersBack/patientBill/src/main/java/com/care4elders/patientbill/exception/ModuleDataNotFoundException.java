package com.care4elders.patientbill.exception;

public class ModuleDataNotFoundException extends RuntimeException {
    
    public ModuleDataNotFoundException(String message) {
        super(message);
    }
    
    public ModuleDataNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}