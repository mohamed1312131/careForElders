package com.care4elders.userservice.dto;

import com.care4elders.userservice.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserRequest {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Size(min=8 ,max=8 , message = "Phone number Must Be 8 characters")
    private String phoneNumber;

    private LocalDate birthDate;

    private String profileImage;
    private Role role;
    private Boolean  twoFactorEnabled;

    // Add these new fields
    private String emergencyContactName;
    private String emergencyContactPhone;

    @Email(message = "Invalid emergency contact email format")
    private String emergencyContactEmail;
}
