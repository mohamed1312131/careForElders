package com.care4elders.nutrition.controller;

import com.care4elders.nutrition.DTO.NutritionPlanDTO;
import com.care4elders.nutrition.entity.MealSchedule;
import com.care4elders.nutrition.service.NutritionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nutrition-plans")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class NutritionPlanController {

    private final NutritionPlanService nutritionPlanService;

    // ================================
    // STANDARD CRUD OPERATIONS
    // ================================

    /**
     * Create a new nutrition plan
     */
    @PostMapping
    public ResponseEntity<NutritionPlanDTO> createNutritionPlan(@Valid @RequestBody NutritionPlanDTO dto) {
        try {
            log.info("Creating nutrition plan for user: {}", dto.getUserId());
            NutritionPlanDTO createdPlan = nutritionPlanService.createNutritionPlan(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlan);
        } catch (Exception e) {
            log.error("Error creating nutrition plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all nutrition plans with pagination
     */
    @GetMapping
    public ResponseEntity<List<NutritionPlanDTO>> getAllNutritionPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        try {
            log.info("Fetching all nutrition plans - page: {}, size: {}, sortBy: {}", page, size, sortBy);
            List<NutritionPlanDTO> plans = nutritionPlanService.getAllNutritionPlans(page, size, sortBy);
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            log.error("Error fetching nutrition plans", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get nutrition plan by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<NutritionPlanDTO> getNutritionPlanById(@PathVariable String id) {
        try {
            log.info("Fetching nutrition plan with id: {}", id);
            NutritionPlanDTO plan = nutritionPlanService.getNutritionPlanById(id);
            if (plan != null) {
                return ResponseEntity.ok(plan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error fetching nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update nutrition plan
     */
    @PutMapping("/{id}")
    public ResponseEntity<NutritionPlanDTO> updateNutritionPlan(
            @PathVariable String id,
            @Valid @RequestBody NutritionPlanDTO planDTO) {
        try {
            log.info("Updating nutrition plan with id: {}", id);
            NutritionPlanDTO updatedPlan = nutritionPlanService.updateNutritionPlan(id, planDTO);
            if (updatedPlan != null) {
                return ResponseEntity.ok(updatedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error updating nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Partially update nutrition plan
     */
    @PatchMapping("/{id}")
    public ResponseEntity<NutritionPlanDTO> patchNutritionPlan(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        try {
            log.info("Patching nutrition plan with id: {}", id);
            NutritionPlanDTO patchedPlan = nutritionPlanService.patchNutritionPlan(id, updates);
            if (patchedPlan != null) {
                return ResponseEntity.ok(patchedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error patching nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete nutrition plan
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNutritionPlan(@PathVariable String id) {
        try {
            log.info("Deleting nutrition plan with id: {}", id);
            boolean deleted = nutritionPlanService.deleteNutritionPlan(id);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ================================
    // QUERY OPERATIONS
    // ================================

    /**
     * Get nutrition plans by user ID
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NutritionPlanDTO>> getNutritionPlansByUserId(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching nutrition plans for user: {}", userId);
            List<NutritionPlanDTO> plans = nutritionPlanService.getNutritionPlansByUserId(userId, page, size);
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            log.error("Error fetching nutrition plans for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get plans by medical condition
     */
    @GetMapping("/condition/{condition}")
    public ResponseEntity<List<NutritionPlanDTO>> getPlansByMedicalCondition(
            @PathVariable String condition,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Fetching nutrition plans for condition: {}", condition);
            List<NutritionPlanDTO> plans = nutritionPlanService.getPlansByMedicalCondition(condition, page, size);
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            log.error("Error fetching nutrition plans for condition: {}", condition, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search plans by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<NutritionPlanDTO>> searchPlans(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Searching nutrition plans with keyword: {}", keyword);
            List<NutritionPlanDTO> plans = nutritionPlanService.searchPlans(keyword, page, size);
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            log.error("Error searching nutrition plans with keyword: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        try {
            log.info("Fetching nutrition plan statistics");
            long totalPlans = nutritionPlanService.getTotalPlansCount();
            long activePlans = nutritionPlanService.getActivePlansCount();

            Map<String, Long> stats = Map.of(
                    "totalPlans", totalPlans,
                    "activePlans", activePlans,
                    "inactivePlans", totalPlans - activePlans
            );

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ================================
    // AI-POWERED OPERATIONS
    // ================================

    /**
     * Generate monthly nutrition plan using AI
     */
    @PostMapping("/generate")
    public ResponseEntity<NutritionPlanDTO> generateMonthlyPlan(
            @RequestParam String userId,
            @RequestParam String medicalConditions,
            @RequestParam String userEmail) {
        try {
            log.info("Generating monthly nutrition plan for user: {}", userId);
            NutritionPlanDTO generatedPlan = nutritionPlanService.generateMonthlyPlan(userId, medicalConditions, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED).body(generatedPlan);
        } catch (Exception e) {
            log.error("Error generating monthly nutrition plan for user: {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Regenerate plan using AI
     */
    @PostMapping("/{id}/regenerate")
    public ResponseEntity<NutritionPlanDTO> regeneratePlan(@PathVariable String id) {
        try {
            log.info("Regenerating nutrition plan with id: {}", id);
            NutritionPlanDTO regeneratedPlan = nutritionPlanService.regeneratePlan(id);
            if (regeneratedPlan != null) {
                return ResponseEntity.ok(regeneratedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error regenerating nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ================================
    // SOCIAL FEATURES
    // ================================

    /**
     * Like a nutrition plan
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<NutritionPlanDTO> likeNutritionPlan(@PathVariable String id) {
        try {
            log.info("Liking nutrition plan with id: {}", id);
            NutritionPlanDTO likedPlan = nutritionPlanService.likeNutritionPlan(id);
            if (likedPlan != null) {
                return ResponseEntity.ok(likedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error liking nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Dislike a nutrition plan
     */
    @PostMapping("/{id}/dislike")
    public ResponseEntity<NutritionPlanDTO> dislikeNutritionPlan(@PathVariable String id) {
        try {
            log.info("Disliking nutrition plan with id: {}", id);
            NutritionPlanDTO dislikedPlan = nutritionPlanService.dislikeNutritionPlan(id);
            if (dislikedPlan != null) {
                return ResponseEntity.ok(dislikedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error disliking nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Add comment to nutrition plan
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<NutritionPlanDTO> addComment(
            @PathVariable String id,
            @RequestBody Map<String, String> commentData) {
        try {
            String comment = commentData.get("comment");
            String userId = commentData.getOrDefault("userId", "anonymous");

            log.info("Adding comment to nutrition plan with id: {}", id);
            NutritionPlanDTO updatedPlan = nutritionPlanService.addComment(id, comment, userId);
            if (updatedPlan != null) {
                return ResponseEntity.ok(updatedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error adding comment to nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get comments for nutrition plan
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable String id) {
        try {
            log.info("Fetching comments for nutrition plan with id: {}", id);
            List<Map<String, Object>> comments = nutritionPlanService.getComments(id);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            log.error("Error fetching comments for nutrition plan with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Delete comment from nutrition plan
     */
    @DeleteMapping("/{id}/comments/{commentId}")
    public ResponseEntity<NutritionPlanDTO> deleteComment(
            @PathVariable String id,
            @PathVariable String commentId) {
        try {
            log.info("Deleting comment {} from nutrition plan {}", commentId, id);
            NutritionPlanDTO updatedPlan = nutritionPlanService.deleteComment(id, commentId);
            if (updatedPlan != null) {
                return ResponseEntity.ok(updatedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error deleting comment from nutrition plan", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ================================
    // EMAIL FEATURES
    // ================================

    /**
     * Toggle email reminders for a plan
     */
    @PatchMapping("/{id}/email-reminders")
    public ResponseEntity<NutritionPlanDTO> toggleEmailReminders(
            @PathVariable String id,
            @RequestBody Map<String, Boolean> request) {
        try {
            boolean enabled = request.getOrDefault("enabled", true);
            log.info("Toggling email reminders for nutrition plan: {}", id);
            NutritionPlanDTO updatedPlan = nutritionPlanService.toggleEmailReminders(id, enabled);
            if (updatedPlan != null) {
                return ResponseEntity.ok(updatedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error toggling email reminders for plan: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Update meal schedule for a plan
     */
    @PutMapping("/{id}/meal-schedule")
    public ResponseEntity<NutritionPlanDTO> updateMealSchedule(
            @PathVariable String id,
            @RequestBody MealSchedule schedule) {
        try {
            log.info("Updating meal schedule for nutrition plan: {}", id);
            NutritionPlanDTO updatedPlan = nutritionPlanService.updateMealSchedule(id, schedule);
            if (updatedPlan != null) {
                return ResponseEntity.ok(updatedPlan);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error updating meal schedule for plan: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Send test reminder
     */
    @PostMapping("/{id}/test-reminder")
    public ResponseEntity<Map<String, String>> sendTestReminder(
            @PathVariable String id,
            @RequestParam String mealType) {
        try {
            log.info("Sending test reminder for nutrition plan: {} and meal: {}", id, mealType);
            boolean sent = nutritionPlanService.sendTestReminder(id, mealType);
            if (sent) {
                return ResponseEntity.ok(Map.of("message", "Test reminder sent successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to send test reminder"));
            }
        } catch (Exception e) {
            log.error("Error sending test reminder for plan: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * Send immediate reminder
     */
    @PostMapping("/{id}/immediate-reminder")
    public ResponseEntity<Map<String, String>> sendImmediateReminder(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String mealType = request.get("mealType");
            String customMessage = request.get("customMessage");

            log.info("Sending immediate reminder for nutrition plan: {}", id);
            boolean sent = nutritionPlanService.sendImmediateReminder(id, mealType, customMessage);
            if (sent) {
                return ResponseEntity.ok(Map.of("message", "Immediate reminder sent successfully"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("error", "Failed to send immediate reminder"));
            }
        } catch (Exception e) {
            log.error("Error sending immediate reminder for plan: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    // ================================
    // BULK OPERATIONS
    // ================================

    /**
     * Bulk delete nutrition plans
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkDeletePlans(@RequestBody List<String> planIds) {
        try {
            log.info("Bulk deleting {} nutrition plans", planIds.size());
            int deletedCount = nutritionPlanService.bulkDeletePlans(planIds);
            return ResponseEntity.ok(Map.of(
                    "message", "Bulk delete completed",
                    "deletedCount", deletedCount,
                    "totalRequested", planIds.size()
            ));
        } catch (Exception e) {
            log.error("Error bulk deleting nutrition plans", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }

    /**
     * Bulk update nutrition plans
     */
    @PatchMapping("/bulk")
    public ResponseEntity<Map<String, Object>> bulkUpdatePlans(
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> planIds = (List<String>) request.get("planIds");
            @SuppressWarnings("unchecked")
            Map<String, Object> updates = (Map<String, Object>) request.get("updates");

            log.info("Bulk updating {} nutrition plans", planIds.size());
            int updatedCount = nutritionPlanService.bulkUpdatePlans(planIds, updates);
            return ResponseEntity.ok(Map.of(
                    "message", "Bulk update completed",
                    "updatedCount", updatedCount,
                    "totalRequested", planIds.size()
            ));
        } catch (Exception e) {
            log.error("Error bulk updating nutrition plans", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
        }
    }
}