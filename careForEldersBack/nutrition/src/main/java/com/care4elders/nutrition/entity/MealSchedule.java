package com.care4elders.nutrition.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MealSchedule {

    private String breakfastTime;
    private String lunchTime;
    private String dinnerTime;
    private List<String> snackTimes;

    // ================================
    // UTILITY METHODS ONLY
    // ================================

    /**
     * Check if the schedule has any meal times set
     */
    public boolean hasAnyMealTimes() {
        return (breakfastTime != null && !breakfastTime.isEmpty()) ||
                (lunchTime != null && !lunchTime.isEmpty()) ||
                (dinnerTime != null && !dinnerTime.isEmpty()) ||
                (snackTimes != null && !snackTimes.isEmpty());
    }

    /**
     * Get default meal schedule
     */
    public static MealSchedule getDefaultSchedule() {
        MealSchedule schedule = new MealSchedule();
        schedule.setBreakfastTime("08:00");
        schedule.setLunchTime("13:00");
        schedule.setDinnerTime("19:00");
        schedule.setSnackTimes(List.of("10:30", "16:00"));
        return schedule;
    }
}