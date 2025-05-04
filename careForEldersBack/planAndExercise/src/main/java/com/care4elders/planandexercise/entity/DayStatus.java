package com.care4elders.planandexercise.entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DayStatus {
    private boolean completed;
    private LocalDateTime completionDate;
    private int actualDurationMinutes;
    private LocalDateTime actualStartDateTime;
    private LocalDateTime actualEndDateTime;
    private String patientNotes;
    private List<String> completedExerciseIds;
    private int perceivedDifficulty; // Scale 1-5
    private String coachComments;
}