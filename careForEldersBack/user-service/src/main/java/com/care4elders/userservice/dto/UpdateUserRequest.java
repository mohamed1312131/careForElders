package com.care4elders.userservice.dto;

import com.care4elders.userservice.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    @Email(message = "Invalid email format")
    private String email;
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    @Size(min = 8, max = 8, message = "Phone number must be 8 characters")
    private String phoneNumber;
    private LocalDate birthDate;
    private String profileImage;
    private Role role;

}
