package com.care4elders.nutrition.controller;

import com.care4elders.nutrition.DTO.NutritionPlanDTO;
import com.care4elders.nutrition.entity.NutritionPlan;
import com.care4elders.nutrition.repository.NutritionPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nutrition")
@CrossOrigin(origins = "*")
public class NutritionPlanController {

    @Autowired
    private NutritionPlanRepository repository;

    // Convert Entity to DTO (includes all fields)
    private NutritionPlanDTO convertToDTO(NutritionPlan plan) {
        NutritionPlanDTO dto = new NutritionPlanDTO();
        dto.setId(plan.getId());
        dto.setMeal(plan.getMeal());
        dto.setDescription(plan.getDescription());
        dto.setCalories(plan.getCalories());
        dto.setPictureUrl(plan.getPictureUrl());
        dto.setMealTime(plan.getMealTime());
        dto.setNotes(plan.getNotes());
        dto.setRecommendedAgeGroup(plan.getRecommendedAgeGroup());
        dto.setIngredients(plan.getIngredients());
        dto.setComments(plan.getComments());
        dto.setLikes(plan.getLikes());
        dto.setDislikes(plan.getDislikes());

        return dto;
    }

    // Convert DTO to Entity (includes all fields)
    private NutritionPlan convertToEntity(NutritionPlanDTO dto) {
        NutritionPlan plan = new NutritionPlan();
        plan.setMeal(dto.getMeal());
        plan.setDescription(dto.getDescription());
        plan.setCalories(dto.getCalories());
        plan.setPictureUrl(dto.getPictureUrl());
        plan.setMealTime(dto.getMealTime());
        plan.setNotes(dto.getNotes());
        plan.setRecommendedAgeGroup(dto.getRecommendedAgeGroup());
        plan.setIngredients(dto.getIngredients());
        plan.setComments(dto.getComments());
        plan.setLikes(dto.getLikes());
        plan.setDislikes(dto.getDislikes());

        return plan;
    }

    @GetMapping
    public List<NutritionPlanDTO> getAllPlans() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<NutritionPlanDTO> addPlan(@RequestBody NutritionPlanDTO dto) {
        NutritionPlan plan = convertToEntity(dto);
        // MongoDB will auto-generate the String ID
        NutritionPlan saved = repository.save(plan);
        return ResponseEntity.ok(convertToDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NutritionPlanDTO> getPlan(@PathVariable String id) {
        return repository.findById(id)
                .map(plan -> ResponseEntity.ok(convertToDTO(plan)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<NutritionPlanDTO> updatePlan(
            @PathVariable String id,
            @RequestBody NutritionPlanDTO dto) {
        return repository.findById(id)
                .map(existingPlan -> {
                    existingPlan.setMeal(dto.getMeal());
                    existingPlan.setDescription(dto.getDescription());
                    existingPlan.setCalories(dto.getCalories());
                    existingPlan.setPictureUrl(dto.getPictureUrl());
                    existingPlan.setMealTime(dto.getMealTime());
                    existingPlan.setNotes(dto.getNotes());
                    existingPlan.setRecommendedAgeGroup(dto.getRecommendedAgeGroup());
                    existingPlan.setIngredients(dto.getIngredients());


                    NutritionPlan updated = repository.save(existingPlan);
                    return ResponseEntity.ok(convertToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/{id}/like")
    public ResponseEntity<NutritionPlanDTO> likePlan(@PathVariable String id) {
        return repository.findById(id)
                .map(plan -> {
                    plan.setLikes(plan.getLikes() + 1);
                    NutritionPlan saved = repository.save(plan);
                    return ResponseEntity.ok(convertToDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<NutritionPlanDTO> dislikePlan(@PathVariable String id) {
        return repository.findById(id)
                .map(plan -> {
                    plan.setDislikes(plan.getDislikes() + 1);
                    NutritionPlan saved = repository.save(plan);
                    return ResponseEntity.ok(convertToDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<NutritionPlanDTO> addComment(
            @PathVariable String id,
            @RequestBody String comment) {
        return repository.findById(id)
                .map(plan -> {
                    List<String> comments = plan.getComments() != null ?
                            plan.getComments() : new ArrayList<>();
                    comments.add(comment);
                    plan.setComments(comments);
                    NutritionPlan saved = repository.save(plan);
                    return ResponseEntity.ok(convertToDTO(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
