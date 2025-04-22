package com.care4elders.planandexercise.entity;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Document(collection = "care_assistants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareAssistant {
    @Id
    private String id;

    @NotBlank
    @Indexed
    private String userId;  // Reference to user-service
}
