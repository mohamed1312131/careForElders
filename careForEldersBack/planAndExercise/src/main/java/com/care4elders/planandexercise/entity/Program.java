package com.care4elders.planandexercise.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Program {
    @Id
    private String id;
    private String name;
    private String description;
    private String doctorId;  // Reference to User service's doctor ID
    @Transient
    private List<ProgramDay> days = new ArrayList<>(); // Initialize here
    private String status; // DRAFT, PUBLISHED, ARCHIVED
    private int versionNumber = 1;
    private List<String> tags;
    private int durationWeeks;
    private String programCategory; // Post-Surgery, Weight Loss, Senior Fitness
    private LocalDateTime updatedAt;
    private boolean isTemplate;
    private List<String> prerequisitePrograms;
    private String programImage;
    private int estimatedTotalDurationHours;
    @Version
    private Long version;
    private boolean isDeleted = false;
    private LocalDateTime deletedAt;
    private List<String> programDayIds = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime updatedDate;

    @CreatedBy
    private String createdBy;
}