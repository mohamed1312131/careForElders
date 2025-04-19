package com.care4elders.userservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "verification_tokens")
public class VerificationToken {

    @Id
    private String id;

    private String token;

    @DBRef
    private User user;

    private LocalDateTime expiryDate;
}
