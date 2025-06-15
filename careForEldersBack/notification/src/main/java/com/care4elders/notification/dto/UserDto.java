package com.care4elders.notification.dto;

import lombok.Data;

@Data
public class UserDto {
    private String id;
    private String firstName;
    private String lastName;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactEmail;
}