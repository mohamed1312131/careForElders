package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MedicalRecord {
    private String id;
    private String userId;
    private List<MedicalNote> notes;

    @Data
    public static class MedicalNote {
        private String content;
        private String authorId;
        private LocalDate createdDate;
    }
}
