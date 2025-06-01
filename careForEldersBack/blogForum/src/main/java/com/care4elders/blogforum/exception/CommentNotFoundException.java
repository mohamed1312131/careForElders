package com.care4elders.blogforum.exception;

public class CommentNotFoundException extends RuntimeException {
    
    public CommentNotFoundException(String message) {
        super(message);
    }
}