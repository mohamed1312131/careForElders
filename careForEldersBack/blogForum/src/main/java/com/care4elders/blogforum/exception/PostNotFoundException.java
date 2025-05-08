package com.care4elders.blogforum.exception;

public class PostNotFoundException extends RuntimeException {
    
    public PostNotFoundException(String message) {
        super(message);
    }
}
