package com.care4elders.subscription.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String username;
    private String email;
    private String role;  // "ADMIN", "DOCTOR", "PATIENT", etc.
    private boolean active;
    private String currentSubscriptionId;  // ID of active UserSubscription (in subscription service)
    private String currentPlanName;
    // Add other fields that your user service returns
    // (phone number, address, etc.) as needed
}