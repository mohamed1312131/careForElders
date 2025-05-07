package com.care4elders.userservice.dto;

import com.care4elders.userservice.entity.Role;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Role role;
    private LocalDate birthDate;
    private String profileImage;
    private String specialization;
}
