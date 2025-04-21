package com.care4elders.userservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String resetToken;
    private LocalDate birthDate;
    private String profileImage;
    public String phoneNumber;
    private Role role;
    private boolean enabled = false; // Email verification status
    private String verificationToken;
    private LocalDateTime tokenExpiryDate;
    private String resetPasswordToken;

    public User(Object o, String admin, String smith, String mail, String admin1, Object o1, String s, Role role) {
    }

    public User(Object o, String admin, String smith, String mail, String admin1, String s, Role role) {
    }
}
