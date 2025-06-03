package com.care4elders.nutrition.repository;

import com.care4elders.nutrition.entity.NutritionPlan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NutritionPlanRepository extends MongoRepository<NutritionPlan, String> {

    // ================================
    // BASIC CRUD QUERIES
    // ================================

    List<NutritionPlan> findByUserId(String userId);
    List<NutritionPlan> findByUserId(String userId, Pageable pageable);
    Page<NutritionPlan> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<NutritionPlan> findByUserEmail(String userEmail);
    List<NutritionPlan> findByUserEmail(String userEmail, Pageable pageable);

    Optional<NutritionPlan> findByIdAndUserId(String id, String userId);

    // ================================
    // STATUS-BASED QUERIES
    // ================================

    List<NutritionPlan> findByActiveTrue();
    List<NutritionPlan> findByActiveTrue(Pageable pageable);
    Page<NutritionPlan> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    List<NutritionPlan> findByActiveFalse();
    List<NutritionPlan> findByActiveFalse(Pageable pageable);

    long countByActiveTrue();
    long countByActiveFalse();

    // **CRITICAL METHOD FOR MEAL REMINDERS**
    List<NutritionPlan> findByActiveTrueAndEmailRemindersEnabledTrue();
    List<NutritionPlan> findByActiveTrueAndEmailRemindersEnabledTrue(Pageable pageable);

    List<NutritionPlan> findByEmailRemindersEnabledTrue();
    List<NutritionPlan> findByEmailRemindersEnabledTrue(Pageable pageable);

    List<NutritionPlan> findByUserIdAndActiveTrue(String userId);
    List<NutritionPlan> findByUserIdAndActiveTrue(String userId, Pageable pageable);

    List<NutritionPlan> findByUserIdAndActiveFalse(String userId);

    // ================================
    // SEARCH QUERIES
    // ================================

    List<NutritionPlan> findByMedicalConditionsContainingIgnoreCase(String condition);
    List<NutritionPlan> findByMedicalConditionsContainingIgnoreCase(String condition, Pageable pageable);
    Page<NutritionPlan> findByMedicalConditionsContainingIgnoreCaseOrderByCreatedAtDesc(String condition, Pageable pageable);

    List<NutritionPlan> findByDietaryPreferencesContainingIgnoreCase(String preference);
    List<NutritionPlan> findByDietaryPreferencesContainingIgnoreCase(String preference, Pageable pageable);

    List<NutritionPlan> findByAllergiesContainingIgnoreCase(String allergy);
    List<NutritionPlan> findByAllergiesContainingIgnoreCase(String allergy, Pageable pageable);

    // Combined search queries
    List<NutritionPlan> findByMedicalConditionsContainingIgnoreCaseOrDietaryPreferencesContainingIgnoreCase(
            String medicalCondition, String dietaryPreference);

    List<NutritionPlan> findByMedicalConditionsContainingIgnoreCaseOrDietaryPreferencesContainingIgnoreCase(
            String medicalCondition, String dietaryPreference, Pageable pageable);

    Page<NutritionPlan> findByMedicalConditionsContainingIgnoreCaseOrDietaryPreferencesContainingIgnoreCaseOrderByCreatedAtDesc(
            String medicalCondition, String dietaryPreference, Pageable pageable);

    // Multi-field search
    List<NutritionPlan> findByMedicalConditionsContainingIgnoreCaseOrDietaryPreferencesContainingIgnoreCaseOrAllergiesContainingIgnoreCase(
            String medicalCondition, String dietaryPreference, String allergy, Pageable pageable);

    // ================================
    // DATE/TIME QUERIES
    // ================================

    List<NutritionPlan> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<NutritionPlan> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    List<NutritionPlan> findByCreatedAtAfter(LocalDateTime date);
    List<NutritionPlan> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);

    List<NutritionPlan> findByCreatedAtBefore(LocalDateTime date);
    List<NutritionPlan> findByCreatedAtBefore(LocalDateTime date, Pageable pageable);

    List<NutritionPlan> findByUpdatedAtAfter(LocalDateTime date);
    List<NutritionPlan> findByUpdatedAtAfter(LocalDateTime date, Pageable pageable);

    List<NutritionPlan> findByUpdatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<NutritionPlan> findByLastReminderSentBefore(LocalDateTime date);
    List<NutritionPlan> findByLastReminderSentAfter(LocalDateTime date);
    List<NutritionPlan> findByLastReminderSentIsNull();
    List<NutritionPlan> findByLastReminderSentIsNotNull();

    // ================================
    // SOCIAL FEATURES QUERIES
    // ================================

    List<NutritionPlan> findByLikesGreaterThan(Integer likes);
    List<NutritionPlan> findByLikesGreaterThan(Integer likes, Pageable pageable);

    List<NutritionPlan> findByDislikesGreaterThan(Integer dislikes);
    List<NutritionPlan> findByDislikesGreaterThan(Integer dislikes, Pageable pageable);

    List<NutritionPlan> findByLikesGreaterThanAndDislikesLessThan(Integer minLikes, Integer maxDislikes);
    List<NutritionPlan> findByLikesGreaterThanAndDislikesLessThan(Integer minLikes, Integer maxDislikes, Pageable pageable);

    // Custom queries for social features
    @Query("{ 'likes': { $gte: ?0 }, 'dislikes': { $lt: ?1 } }")
    List<NutritionPlan> findPopularPlans(Integer minLikes, Integer maxDislikes);

    @Query("{ 'likes': { $gte: ?0 }, 'dislikes': { $lt: ?1 } }")
    List<NutritionPlan> findPopularPlans(Integer minLikes, Integer maxDislikes, Pageable pageable);

    @Query("{ 'comments': { $exists: true, $not: { $size: 0 } } }")
    List<NutritionPlan> findPlansWithComments();

    @Query("{ 'comments': { $exists: true, $not: { $size: 0 } } }")
    List<NutritionPlan> findPlansWithComments(Pageable pageable);

    @Query("{ 'comments': { $exists: false } }")
    List<NutritionPlan> findPlansWithoutComments();

    // ================================
    // HEALTH-SPECIFIC QUERIES
    // ================================

    List<NutritionPlan> findByPlanDuration(Integer duration);
    List<NutritionPlan> findByPlanDuration(Integer duration, Pageable pageable);

    List<NutritionPlan> findByPlanDurationBetween(Integer minDuration, Integer maxDuration);
    List<NutritionPlan> findByPlanDurationBetween(Integer minDuration, Integer maxDuration, Pageable pageable);

    List<NutritionPlan> findByPlanDurationGreaterThan(Integer duration);
    List<NutritionPlan> findByPlanDurationLessThan(Integer duration);

    List<NutritionPlan> findByTargetCalories(Integer calories);
    List<NutritionPlan> findByTargetCalories(Integer calories, Pageable pageable);

    List<NutritionPlan> findByTargetCaloriesBetween(Integer minCalories, Integer maxCalories);
    List<NutritionPlan> findByTargetCaloriesBetween(Integer minCalories, Integer maxCalories, Pageable pageable);

    List<NutritionPlan> findByTargetCaloriesGreaterThan(Integer calories);
    List<NutritionPlan> findByTargetCaloriesLessThan(Integer calories);

    // Combined health queries
    @Query("{ 'medicalConditions': { $regex: ?0, $options: 'i' }, 'active': true }")
    List<NutritionPlan> findActiveByMedicalCondition(String condition);

    @Query("{ 'medicalConditions': { $regex: ?0, $options: 'i' }, 'active': true }")
    List<NutritionPlan> findActiveByMedicalCondition(String condition, Pageable pageable);

    @Query("{ 'dietaryPreferences': { $regex: ?0, $options: 'i' }, 'active': true }")
    List<NutritionPlan> findActiveByDietaryPreference(String preference);

    @Query("{ 'dietaryPreferences': { $regex: ?0, $options: 'i' }, 'active': true }")
    List<NutritionPlan> findActiveByDietaryPreference(String preference, Pageable pageable);

    @Query("{ 'allergies': { $regex: ?0, $options: 'i' }, 'active': true }")
    List<NutritionPlan> findActiveByAllergy(String allergy);

    // ================================
    // AGGREGATION QUERIES
    // ================================

    @Query(value = "{ 'userId': ?0 }", count = true)
    long countByUserId(String userId);

    @Query(value = "{ 'userId': ?0, 'active': true }", count = true)
    long countActiveByUserId(String userId);

    @Query(value = "{ 'medicalConditions': { $regex: ?0, $options: 'i' } }", count = true)
    long countByMedicalCondition(String condition);

    @Query(value = "{ 'medicalConditions': { $regex: ?0, $options: 'i' }, 'active': true }", count = true)
    long countActiveByMedicalCondition(String condition);

    @Query(value = "{ 'dietaryPreferences': { $regex: ?0, $options: 'i' } }", count = true)
    long countByDietaryPreference(String preference);

    @Query(value = "{ 'active': true, 'emailRemindersEnabled': true }", count = true)
    long countActiveWithEmailReminders();

    @Query(value = "{ 'createdAt': { $gte: ?0, $lte: ?1 } }", count = true)
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query(value = "{ 'likes': { $gte: ?0 } }", count = true)
    long countByLikesGreaterThanEqual(Integer likes);

    // ================================
    // BULK OPERATIONS
    // ================================

    void deleteByUserId(String userId);
    void deleteByUserIdAndActiveTrue(String userId);
    void deleteByUserIdAndActiveFalse(String userId);

    void deleteByActiveAndCreatedAtBefore(boolean active, LocalDateTime date);
    void deleteByActiveFalseAndCreatedAtBefore(LocalDateTime date);

    @Query("{ 'userId': ?0, 'active': true }")
    List<NutritionPlan> findActiveByUserId(String userId);

    @Query("{ 'userId': ?0, 'active': true }")
    List<NutritionPlan> findActiveByUserId(String userId, Pageable pageable);

    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 }, 'active': true }")
    List<NutritionPlan> findActiveBetweenDates(LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'createdAt': { $gte: ?0, $lte: ?1 }, 'active': true }")
    List<NutritionPlan> findActiveBetweenDates(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // ================================
    // EMAIL REMINDER SPECIFIC QUERIES
    // ================================

    @Query("{ 'active': true, 'emailRemindersEnabled': true, 'userEmail': { $exists: true, $ne: null, $ne: '' } }")
    List<NutritionPlan> findActiveWithValidEmailForReminders();

    @Query("{ 'active': true, 'emailRemindersEnabled': true, 'mealSchedule': { $exists: true, $ne: null } }")
    List<NutritionPlan> findActiveWithEmailAndMealSchedule();

    @Query("{ 'active': true, 'emailRemindersEnabled': true, 'lastReminderSent': { $lt: ?0 } }")
    List<NutritionPlan> findActiveWithEmailAndLastReminderBefore(LocalDateTime date);

    @Query("{ 'active': true, 'emailRemindersEnabled': true, 'lastReminderSent': { $exists: false } }")
    List<NutritionPlan> findActiveWithEmailAndNoReminderSent();

    // ================================
    // ADVANCED SEARCH QUERIES
    // ================================

    @Query("{ $or: [ " +
            "{ 'medicalConditions': { $regex: ?0, $options: 'i' } }, " +
            "{ 'dietaryPreferences': { $regex: ?0, $options: 'i' } }, " +
            "{ 'allergies': { $regex: ?0, $options: 'i' } }, " +
            "{ 'aiGeneratedPlan': { $regex: ?0, $options: 'i' } } " +
            "], 'active': true }")
    List<NutritionPlan> searchActiveByKeyword(String keyword);

    @Query("{ $or: [ " +
            "{ 'medicalConditions': { $regex: ?0, $options: 'i' } }, " +
            "{ 'dietaryPreferences': { $regex: ?0, $options: 'i' } }, " +
            "{ 'allergies': { $regex: ?0, $options: 'i' } }, " +
            "{ 'aiGeneratedPlan': { $regex: ?0, $options: 'i' } } " +
            "], 'active': true }")
    List<NutritionPlan> searchActiveByKeyword(String keyword, Pageable pageable);

    @Query("{ $and: [ " +
            "{ 'userId': ?0 }, " +
            "{ $or: [ " +
            "{ 'medicalConditions': { $regex: ?1, $options: 'i' } }, " +
            "{ 'dietaryPreferences': { $regex: ?1, $options: 'i' } }, " +
            "{ 'allergies': { $regex: ?1, $options: 'i' } } " +
            "] } ] }")
    List<NutritionPlan> searchByUserAndKeyword(String userId, String keyword);

    // ================================
    // STATISTICS QUERIES
    // ================================

    @Query(value = "{ 'createdAt': { $gte: ?0 } }", count = true)
    long countCreatedAfter(LocalDateTime date);

    @Query(value = "{ 'updatedAt': { $gte: ?0 } }", count = true)
    long countUpdatedAfter(LocalDateTime date);

    @Query(value = "{ 'planDuration': { $gte: ?0, $lte: ?1 } }", count = true)
    long countByPlanDurationBetween(Integer minDuration, Integer maxDuration);

    @Query(value = "{ 'targetCalories': { $gte: ?0, $lte: ?1 } }", count = true)
    long countByTargetCaloriesBetween(Integer minCalories, Integer maxCalories);

    // ================================
    // CUSTOM BUSINESS LOGIC QUERIES
    // ================================

    @Query("{ 'userId': ?0, 'active': true, 'createdAt': { $gte: ?1 } }")
    List<NutritionPlan> findRecentActiveByUser(String userId, LocalDateTime since);

    @Query("{ 'emailRemindersEnabled': true, 'active': true, 'mealSchedule.breakfastTime': { $exists: true } }")
    List<NutritionPlan> findWithBreakfastReminders();

    @Query("{ 'emailRemindersEnabled': true, 'active': true, 'mealSchedule.lunchTime': { $exists: true } }")
    List<NutritionPlan> findWithLunchReminders();

    @Query("{ 'emailRemindersEnabled': true, 'active': true, 'mealSchedule.dinnerTime': { $exists: true } }")
    List<NutritionPlan> findWithDinnerReminders();

    @Query("{ 'emailRemindersEnabled': true, 'active': true, 'mealSchedule.snackTimes': { $exists: true, $not: { $size: 0 } } }")
    List<NutritionPlan> findWithSnackReminders();
}