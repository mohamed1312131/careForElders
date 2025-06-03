package com.care4elders.nutrition.service;

import com.care4elders.nutrition.DTO.NutritionPlanDTO;
import com.care4elders.nutrition.entity.NutritionPlan;
import com.care4elders.nutrition.entity.MealSchedule;
import com.care4elders.nutrition.repository.NutritionPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NutritionPlanServiceImpl implements NutritionPlanService {

    private final NutritionPlanRepository nutritionPlanRepository;
    private final HuggingFaceService huggingFaceService;
    private final EmailService emailService;
    private final MealReminderService mealReminderService;

    // Constructor with @Lazy for MealReminderService
    @Autowired
    public NutritionPlanServiceImpl(
            NutritionPlanRepository nutritionPlanRepository,
            HuggingFaceService huggingFaceService,
            EmailService emailService,
            @Lazy MealReminderService mealReminderService) {
        this.nutritionPlanRepository = nutritionPlanRepository;
        this.huggingFaceService = huggingFaceService;
        this.emailService = emailService;
        this.mealReminderService = mealReminderService;
    }

    // ================================
    // STANDARD CRUD OPERATIONS
    // ================================

    @Override
    public NutritionPlanDTO createNutritionPlan(NutritionPlanDTO dto) {
        try {
            log.info("Creating nutrition plan for user: {}", dto.getUserId());

            NutritionPlan plan = convertToEntity(dto);
            plan.setCreatedAt(LocalDateTime.now());
            plan.setUpdatedAt(LocalDateTime.now());
            plan.setActive(true);

            // Initialize social features
            if (plan.getLikes() == null) plan.setLikes(0);
            if (plan.getDislikes() == null) plan.setDislikes(0);
            if (plan.getComments() == null) plan.setComments(new ArrayList<>());

            // Set default email reminders
            if (plan.getUserEmail() != null && !plan.getUserEmail().isEmpty()) {
                plan.setEmailRemindersEnabled(true);
            }

            NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
            log.info("Successfully created nutrition plan with id: {}", savedPlan.getId());

            // Send plan creation confirmation email
            if (savedPlan.getUserEmail() != null && !savedPlan.getUserEmail().isEmpty()) {
                try {
                    emailService.sendPlanCreationConfirmation(savedPlan.getUserEmail(), savedPlan);
                } catch (Exception e) {
                    log.warn("Failed to send plan creation confirmation email", e);
                }
            }

            return convertToDTO(savedPlan);
        } catch (Exception e) {
            log.error("Error creating nutrition plan", e);
            throw new RuntimeException("Failed to create nutrition plan", e);
        }
    }

    @Override
    public List<NutritionPlanDTO> getAllNutritionPlans(int page, int size, String sortBy) {
        try {
            log.info("Fetching all nutrition plans - page: {}, size: {}, sortBy: {}", page, size, sortBy);

            Sort sort = Sort.by(Sort.Direction.DESC, sortBy != null ? sortBy : "createdAt");
            Pageable pageable = PageRequest.of(page, size, sort);

            List<NutritionPlan> plans = nutritionPlanRepository.findAll(pageable).getContent();
            return plans.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all nutrition plans", e);
            throw new RuntimeException("Failed to fetch nutrition plans", e);
        }
    }

    @Override
    public List<NutritionPlanDTO> getAllNutritionPlans() {
        return getAllNutritionPlans(0, 10, "createdAt");
    }

    @Override
    public NutritionPlanDTO getNutritionPlanById(String id) {
        try {
            log.info("Fetching nutrition plan with id: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                return convertToDTO(planOpt.get());
            } else {
                log.warn("Nutrition plan not found with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error fetching nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to fetch nutrition plan", e);
        }
    }

    @Override
    public NutritionPlanDTO updateNutritionPlan(String id, NutritionPlanDTO planDTO) {
        try {
            log.info("Updating nutrition plan with id: {}", id);

            Optional<NutritionPlan> existingPlanOpt = nutritionPlanRepository.findById(id);
            if (existingPlanOpt.isPresent()) {
                NutritionPlan existingPlan = existingPlanOpt.get();

                // Update fields from DTO
                updateEntityFromDTO(existingPlan, planDTO);
                existingPlan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan updatedPlan = nutritionPlanRepository.save(existingPlan);
                log.info("Successfully updated nutrition plan with id: {}", id);

                return convertToDTO(updatedPlan);
            } else {
                log.warn("Nutrition plan not found for update with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error updating nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to update nutrition plan", e);
        }
    }

    @Override
    public NutritionPlanDTO patchNutritionPlan(String id, Map<String, Object> updates) {
        try {
            log.info("Patching nutrition plan with id: {}", id);

            Optional<NutritionPlan> existingPlanOpt = nutritionPlanRepository.findById(id);
            if (existingPlanOpt.isPresent()) {
                NutritionPlan existingPlan = existingPlanOpt.get();

                // Apply partial updates
                applyPatchUpdates(existingPlan, updates);
                existingPlan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan patchedPlan = nutritionPlanRepository.save(existingPlan);
                log.info("Successfully patched nutrition plan with id: {}", id);

                return convertToDTO(patchedPlan);
            } else {
                log.warn("Nutrition plan not found for patch with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error patching nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to patch nutrition plan", e);
        }
    }

    @Override
    public boolean deleteNutritionPlan(String id) {
        try {
            log.info("Deleting nutrition plan with id: {}", id);

            if (nutritionPlanRepository.existsById(id)) {
                nutritionPlanRepository.deleteById(id);
                log.info("Successfully deleted nutrition plan with id: {}", id);
                return true;
            } else {
                log.warn("Nutrition plan not found for deletion with id: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error deleting nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to delete nutrition plan", e);
        }
    }

    @Override
    public NutritionPlanDTO generateMonthlyPlan(String userId, String medicalConditions, String userEmail) {
        try {
            log.info("Generating monthly nutrition plan for user: {}", userId);

            // Generate AI-powered plan
            String aiGeneratedPlan = huggingFaceService.generateNutritionPlan(medicalConditions);

            // Create nutrition plan entity
            NutritionPlan plan = new NutritionPlan();
            plan.setUserId(userId);
            plan.setUserEmail(userEmail);
            plan.setMedicalConditions(medicalConditions);
            plan.setAiGeneratedPlan(aiGeneratedPlan);
            plan.setCreatedAt(LocalDateTime.now());
            plan.setUpdatedAt(LocalDateTime.now());
            plan.setActive(true);
            plan.setLikes(0);
            plan.setDislikes(0);
            plan.setComments(new ArrayList<>());
            plan.setEmailRemindersEnabled(true);
            plan.setPlanDuration(30);
            plan.setTargetCalories(2000);

            // Set default meal schedule
            if (plan.getMealSchedule() == null) {
                plan.setMealSchedule(MealSchedule.getDefaultSchedule());
            }

            NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
            log.info("Successfully generated monthly nutrition plan with id: {}", savedPlan.getId());

            return convertToDTO(savedPlan);
        } catch (Exception e) {
            log.error("Error generating monthly nutrition plan for user: {}", userId, e);
            throw new RuntimeException("Failed to generate monthly nutrition plan", e);
        }
    }

    @Override
    public boolean sendTestReminder(String id, String mealType) {
        try {
            log.info("Sending test reminder for nutrition plan: {} and meal: {}", id, mealType);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();

                if (plan.getUserEmail() != null && !plan.getUserEmail().isEmpty()) {
                    mealReminderService.sendTestReminder(plan, mealType);
                    return true;
                } else {
                    log.warn("No email address found for plan: {}", id);
                    return false;
                }
            } else {
                log.warn("Nutrition plan not found for test reminder with id: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending test reminder for plan: {}", id, e);
            throw new RuntimeException("Failed to send test reminder", e);
        }
    }

    @Override
    public NutritionPlan findById(String id) {
        return nutritionPlanRepository.findById(id).orElse(null);
    }

    // ================================
    // PRIVATE HELPER METHODS
    // ================================

    private NutritionPlanDTO convertToDTO(NutritionPlan plan) {
        NutritionPlanDTO dto = new NutritionPlanDTO();

        dto.setId(plan.getId());
        dto.setUserId(plan.getUserId());
        dto.setUserEmail(plan.getUserEmail());
        dto.setMedicalConditions(plan.getMedicalConditions());
        dto.setDietaryPreferences(plan.getDietaryPreferences());
        dto.setAllergies(plan.getAllergies());
        dto.setAiGeneratedPlan(plan.getAiGeneratedPlan());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        dto.setLastReminderSent(plan.getLastReminderSent());
        dto.setActive(plan.isActive());
        dto.setEmailRemindersEnabled(plan.isEmailRemindersEnabled());
        dto.setLikes(plan.getLikes());
        dto.setDislikes(plan.getDislikes());
        dto.setComments(plan.getComments());
        dto.setMealSchedule(plan.getMealSchedule());
        dto.setPlanDuration(plan.getPlanDuration());
        dto.setTargetCalories(plan.getTargetCalories());

        // Set content for backward compatibility
        if (plan.getAiGeneratedPlan() != null) {
            dto.setContent(plan.getAiGeneratedPlan());
        }

        return dto;
    }

    private NutritionPlan convertToEntity(NutritionPlanDTO dto) {
        NutritionPlan plan = new NutritionPlan();

        plan.setId(dto.getId());
        plan.setUserId(dto.getUserId());
        plan.setUserEmail(dto.getUserEmail());
        plan.setMedicalConditions(dto.getMedicalConditions());
        plan.setDietaryPreferences(dto.getDietaryPreferences());
        plan.setAllergies(dto.getAllergies());
        plan.setAiGeneratedPlan(dto.getAiGeneratedPlan());
        plan.setCreatedAt(dto.getCreatedAt());
        plan.setUpdatedAt(dto.getUpdatedAt());
        plan.setLastReminderSent(dto.getLastReminderSent());
        plan.setActive(dto.isActive());
        plan.setEmailRemindersEnabled(dto.isEmailRemindersEnabled());
        plan.setLikes(dto.getLikes());
        plan.setDislikes(dto.getDislikes());
        plan.setComments(dto.getComments());
        plan.setMealSchedule(dto.getMealSchedule());
        plan.setPlanDuration(dto.getPlanDuration());
        plan.setTargetCalories(dto.getTargetCalories());

        if (dto.getContent() != null && plan.getAiGeneratedPlan() == null) {
            plan.setAiGeneratedPlan(dto.getContent());
        }

        return plan;
    }

    private void updateEntityFromDTO(NutritionPlan entity, NutritionPlanDTO dto) {
        if (dto.getUserEmail() != null) entity.setUserEmail(dto.getUserEmail());
        if (dto.getMedicalConditions() != null) entity.setMedicalConditions(dto.getMedicalConditions());
        if (dto.getDietaryPreferences() != null) entity.setDietaryPreferences(dto.getDietaryPreferences());
        if (dto.getAllergies() != null) entity.setAllergies(dto.getAllergies());
        if (dto.getAiGeneratedPlan() != null) entity.setAiGeneratedPlan(dto.getAiGeneratedPlan());
        if (dto.getContent() != null && dto.getAiGeneratedPlan() == null) entity.setAiGeneratedPlan(dto.getContent());

        entity.setActive(dto.isActive());
        entity.setEmailRemindersEnabled(dto.isEmailRemindersEnabled());

        if (dto.getMealSchedule() != null) entity.setMealSchedule(dto.getMealSchedule());
        if (dto.getPlanDuration() != null) entity.setPlanDuration(dto.getPlanDuration());
        if (dto.getTargetCalories() != null) entity.setTargetCalories(dto.getTargetCalories());

        if (dto.getLikes() != null) entity.setLikes(dto.getLikes());
        if (dto.getDislikes() != null) entity.setDislikes(dto.getDislikes());
        if (dto.getComments() != null) entity.setComments(dto.getComments());
    }
    // Add these methods to your existing NutritionPlanServiceImpl class:

// ================================
// USER-SPECIFIC QUERIES
// ================================

    @Override
    public List<NutritionPlanDTO> getNutritionPlansByUserId(String userId, int page, int size) {
        try {
            log.info("Fetching nutrition plans for user: {} (page: {}, size: {})", userId, page, size);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<NutritionPlan> plans = nutritionPlanRepository.findByUserId(userId, pageable);

            return plans.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching nutrition plans for user: {}", userId, e);
            throw new RuntimeException("Failed to fetch user nutrition plans", e);
        }
    }

    @Override
    public List<NutritionPlanDTO> getNutritionPlansByUserId(String userId) {
        return getNutritionPlansByUserId(userId, 0, 10);
    }

// ================================
// SEARCH OPERATIONS
// ================================

    @Override
    public List<NutritionPlanDTO> getPlansByMedicalCondition(String condition, int page, int size) {
        try {
            log.info("Fetching nutrition plans for medical condition: {}", condition);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<NutritionPlan> plans = nutritionPlanRepository.findByMedicalConditionsContainingIgnoreCase(condition, pageable);

            return plans.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching nutrition plans for condition: {}", condition, e);
            throw new RuntimeException("Failed to fetch plans by medical condition", e);
        }
    }

    @Override
    public List<NutritionPlanDTO> searchPlans(String keyword, int page, int size) {
        try {
            log.info("Searching nutrition plans with keyword: {}", keyword);

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<NutritionPlan> plans = nutritionPlanRepository.findByMedicalConditionsContainingIgnoreCaseOrDietaryPreferencesContainingIgnoreCase(
                    keyword, keyword, pageable);

            return plans.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error searching nutrition plans with keyword: {}", keyword, e);
            throw new RuntimeException("Failed to search nutrition plans", e);
        }
    }

// ================================
// STATISTICS
// ================================

    @Override
    public long getTotalPlansCount() {
        try {
            return nutritionPlanRepository.count();
        } catch (Exception e) {
            log.error("Error getting total plans count", e);
            throw new RuntimeException("Failed to get total plans count", e);
        }
    }

    @Override
    public long getActivePlansCount() {
        try {
            return nutritionPlanRepository.countByActiveTrue();
        } catch (Exception e) {
            log.error("Error getting active plans count", e);
            throw new RuntimeException("Failed to get active plans count", e);
        }
    }

// ================================
// AI-POWERED OPERATIONS
// ================================

    @Override
    public NutritionPlanDTO regeneratePlan(String id) {
        try {
            log.info("Regenerating nutrition plan with id: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();

                // Regenerate AI plan
                String newAiPlan = huggingFaceService.generateNutritionPlan(plan.getMedicalConditions());
                plan.setAiGeneratedPlan(newAiPlan);
                plan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan savedPlan = nutritionPlanRepository.save(plan);

                // Send plan update notification
                if (savedPlan.getUserEmail() != null && savedPlan.isEmailRemindersEnabled()) {
                    try {
                        emailService.sendPlanUpdateNotification(savedPlan.getUserEmail(), savedPlan);
                    } catch (Exception e) {
                        log.warn("Failed to send plan update notification email", e);
                    }
                }

                return convertToDTO(savedPlan);
            } else {
                log.warn("Nutrition plan not found for regeneration with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error regenerating nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to regenerate nutrition plan", e);
        }
    }

// ================================
// SOCIAL FEATURES
// ================================

    @Override
    public NutritionPlanDTO likeNutritionPlan(String id) {
        try {
            log.info("Liking nutrition plan with id: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();
                plan.setLikes(plan.getLikes() != null ? plan.getLikes() + 1 : 1);
                plan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
                return convertToDTO(savedPlan);
            } else {
                log.warn("Nutrition plan not found for like with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error liking nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to like nutrition plan", e);
        }
    }

    @Override
    public NutritionPlanDTO dislikeNutritionPlan(String id) {
        try {
            log.info("Disliking nutrition plan with id: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();
                plan.setDislikes(plan.getDislikes() != null ? plan.getDislikes() + 1 : 1);
                plan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
                return convertToDTO(savedPlan);
            } else {
                log.warn("Nutrition plan not found for dislike with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error disliking nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to dislike nutrition plan", e);
        }
    }

    @Override
    public NutritionPlanDTO addComment(String id, String comment, String userId) {
        try {
            log.info("Adding comment to nutrition plan with id: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();

                Map<String, Object> commentObj = new HashMap<>();
                commentObj.put("id", UUID.randomUUID().toString());
                commentObj.put("userId", userId);
                commentObj.put("comment", comment);
                commentObj.put("timestamp", LocalDateTime.now().toString());

                if (plan.getComments() == null) {
                    plan.setComments(new ArrayList<>());
                }
                plan.getComments().add(commentObj);
                plan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
                return convertToDTO(savedPlan);
            } else {
                log.warn("Nutrition plan not found for comment with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error adding comment to nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to add comment", e);
        }
    }

    @Override
    public NutritionPlanDTO addComment(String id, String comment) {
        return addComment(id, comment, "anonymous");
    }

    @Override
    public List<Map<String, Object>> getComments(String id) {
        try {
            log.info("Fetching comments for nutrition plan with id: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();
                return plan.getComments() != null ? plan.getComments() : new ArrayList<>();
            } else {
                log.warn("Nutrition plan not found for comments with id: {}", id);
                return new ArrayList<>();
            }
        } catch (Exception e) {
            log.error("Error fetching comments for nutrition plan with id: {}", id, e);
            throw new RuntimeException("Failed to fetch comments", e);
        }
    }

    @Override
    public NutritionPlanDTO deleteComment(String id, String commentId) {
        try {
            log.info("Deleting comment {} from nutrition plan {}", commentId, id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();

                if (plan.getComments() != null) {
                    plan.getComments().removeIf(comment ->
                            commentId.equals(((Map<String, Object>) comment).get("id")));
                    plan.setUpdatedAt(LocalDateTime.now());

                    NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
                    return convertToDTO(savedPlan);
                }
                return convertToDTO(plan);
            } else {
                log.warn("Nutrition plan not found for comment deletion with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error deleting comment from nutrition plan", e);
            throw new RuntimeException("Failed to delete comment", e);
        }
    }

// ================================
// EMAIL FEATURES
// ================================

    @Override
    public NutritionPlanDTO toggleEmailReminders(String id, boolean enabled) {
        try {
            log.info("Toggling email reminders for nutrition plan: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();
                plan.setEmailRemindersEnabled(enabled);
                plan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
                return convertToDTO(savedPlan);
            } else {
                log.warn("Nutrition plan not found for email toggle with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error toggling email reminders for plan: {}", id, e);
            throw new RuntimeException("Failed to toggle email reminders", e);
        }
    }

    @Override
    public NutritionPlanDTO updateMealSchedule(String id, MealSchedule schedule) {
        try {
            log.info("Updating meal schedule for nutrition plan: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();
                plan.setMealSchedule(schedule);
                plan.setUpdatedAt(LocalDateTime.now());

                NutritionPlan savedPlan = nutritionPlanRepository.save(plan);
                return convertToDTO(savedPlan);
            } else {
                log.warn("Nutrition plan not found for meal schedule update with id: {}", id);
                return null;
            }
        } catch (Exception e) {
            log.error("Error updating meal schedule for plan: {}", id, e);
            throw new RuntimeException("Failed to update meal schedule", e);
        }
    }

    @Override
    public boolean sendImmediateReminder(String id, String mealType, String customMessage) {
        try {
            log.info("Sending immediate reminder for nutrition plan: {}", id);

            Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(id);
            if (planOpt.isPresent()) {
                NutritionPlan plan = planOpt.get();

                if (plan.getUserEmail() != null && !plan.getUserEmail().isEmpty()) {
                    mealReminderService.sendImmediateReminder(plan, mealType, customMessage);
                    return true;
                } else {
                    log.warn("No email address found for plan: {}", id);
                    return false;
                }
            } else {
                log.warn("Nutrition plan not found for immediate reminder with id: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending immediate reminder for plan: {}", id, e);
            throw new RuntimeException("Failed to send immediate reminder", e);
        }
    }

// ================================
// BULK OPERATIONS
// ================================

    @Override
    public int bulkDeletePlans(List<String> planIds) {
        try {
            log.info("Bulk deleting {} nutrition plans", planIds.size());

            int deletedCount = 0;
            for (String planId : planIds) {
                if (nutritionPlanRepository.existsById(planId)) {
                    nutritionPlanRepository.deleteById(planId);
                    deletedCount++;
                }
            }

            log.info("Successfully deleted {} out of {} nutrition plans", deletedCount, planIds.size());
            return deletedCount;
        } catch (Exception e) {
            log.error("Error bulk deleting nutrition plans", e);
            throw new RuntimeException("Failed to bulk delete nutrition plans", e);
        }
    }

    @Override
    public int bulkUpdatePlans(List<String> planIds, Map<String, Object> updates) {
        try {
            log.info("Bulk updating {} nutrition plans", planIds.size());

            int updatedCount = 0;
            for (String planId : planIds) {
                Optional<NutritionPlan> planOpt = nutritionPlanRepository.findById(planId);
                if (planOpt.isPresent()) {
                    NutritionPlan plan = planOpt.get();
                    applyPatchUpdates(plan, updates);
                    plan.setUpdatedAt(LocalDateTime.now());
                    nutritionPlanRepository.save(plan);
                    updatedCount++;
                }
            }

            log.info("Successfully updated {} out of {} nutrition plans", updatedCount, planIds.size());
            return updatedCount;
        } catch (Exception e) {
            log.error("Error bulk updating nutrition plans", e);
            throw new RuntimeException("Failed to bulk update nutrition plans", e);
        }
    }

    private void applyPatchUpdates(NutritionPlan entity, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "userEmail":
                    entity.setUserEmail((String) value);
                    break;
                case "medicalConditions":
                    entity.setMedicalConditions((String) value);
                    break;
                case "dietaryPreferences":
                    entity.setDietaryPreferences((String) value);
                    break;
                case "allergies":
                    entity.setAllergies((String) value);
                    break;
                case "aiGeneratedPlan":
                    entity.setAiGeneratedPlan((String) value);
                    break;
                case "content":
                    entity.setAiGeneratedPlan((String) value);
                    break;
                case "active":
                    entity.setActive((Boolean) value);
                    break;
                case "emailRemindersEnabled":
                    entity.setEmailRemindersEnabled((Boolean) value);
                    break;
                case "planDuration":
                    entity.setPlanDuration((Integer) value);
                    break;
                case "targetCalories":
                    entity.setTargetCalories((Integer) value);
                    break;
                case "mealSchedule":
                    if (value instanceof MealSchedule) {
                        entity.setMealSchedule((MealSchedule) value);
                    } else if (value instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> scheduleMap = (Map<String, Object>) value;
                        MealSchedule schedule = new MealSchedule();
                        schedule.setBreakfastTime((String) scheduleMap.get("breakfastTime"));
                        schedule.setLunchTime((String) scheduleMap.get("lunchTime"));
                        schedule.setDinnerTime((String) scheduleMap.get("dinnerTime"));
                        @SuppressWarnings("unchecked")
                        List<String> snackTimes = (List<String>) scheduleMap.get("snackTimes");
                        schedule.setSnackTimes(snackTimes);
                        entity.setMealSchedule(schedule);
                    }
                    break;
                default:
                    log.warn("Unknown field for patch update: {}", key);
            }
        });
    }
}