package com.care4elders.userservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


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
    private String specialization;
    private Role role;
    private boolean enabled = false; // Email verification status
    private String verificationToken;
    private LocalDateTime tokenExpiryDate;
    private String resetPasswordToken;
    private boolean twoFactorEnabled;
    private LocalDateTime LastLogin;
    // For all users (especially patients)
    private List<String> reservationIds;
    // For doctors only
    private List<String> disponibiliteIds;


    // Subscription-related fields
    private String currentSubscriptionId;  // ID of active UserSubscription (in subscription service)
    private String currentPlanName;  // Cached for quick access ("Basic", "Standard", "Premium")
    private List<String> accessibleFeatures;  // Derived from the plan (e.g., ["chat", "nutrition"])


    //medical related
    private String bloodType;
    private String knownAllergies;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private Double height; // in cm
    private Double weight; // in kg
    private String organDonorStatus;
    private String emergencyContactEmail;
    public User(Object o, String admin, String smith, String mail, String admin1, Object o1, String s, Role role) {
    }

    public User(Object o, String admin, String smith, String mail, String admin1, String s, Role role) {
    }


}
