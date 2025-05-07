package com.care4elders.planandexercise.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ExerciseIdsRequest {
    private List<String> ids;

    // Add getter and setter

}