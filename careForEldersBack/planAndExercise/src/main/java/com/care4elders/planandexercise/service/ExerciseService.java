package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.ExerciseDTO;
import com.care4elders.planandexercise.DTO.UserDTO; // Assuming this DTO exists and is correct
import com.care4elders.planandexercise.entity.Exercise;
import com.care4elders.planandexercise.exception.EntityNotFoundException;
import com.care4elders.planandexercise.exception.ServiceUnavailableException;
import com.care4elders.planandexercise.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile; // Import MultipartFile

import java.io.IOException; // Import IOException
import java.time.LocalDateTime;
import java.util.List;
// import java.util.Objects; // Not used from your original snippet
// import java.util.Set; // Not used
// import java.util.stream.Collectors; // Not used

@Service
@RequiredArgsConstructor
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;
    private final RestTemplate restTemplate;
    private final CloudinaryService cloudinaryService; // Inject CloudinaryService
    private static final Logger logger = LoggerFactory.getLogger(ExerciseService.class);

    public Exercise createExercise(ExerciseDTO exerciseDTO,
                                   MultipartFile imageFile,
                                   MultipartFile videoFile,
                                   String doctorId) throws IOException { // Add throws IOException
        //validateDoctor(doctorId);

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                imageUrl = cloudinaryService.uploadImage(imageFile); // Use the convenience method
                logger.info("Image uploaded for exercise {}: {}", exerciseDTO.getName(), imageUrl);
            } catch (IOException e) {
                logger.error("Failed to upload image for exercise {}: {}", exerciseDTO.getName(), e.getMessage(), e);
                throw new IOException("Image upload failed: " + e.getMessage(), e); // Re-throw or handle more gracefully
            }
        }

        String videoTutorialUrl = null;
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                videoTutorialUrl = cloudinaryService.uploadVideo(videoFile); // Use the convenience method
                logger.info("Video uploaded for exercise {}: {}", exerciseDTO.getName(), videoTutorialUrl);
            } catch (IOException e) {
                logger.error("Failed to upload video for exercise {}: {}", exerciseDTO.getName(), e.getMessage(), e);
                throw new IOException("Video upload failed: " + e.getMessage(), e); // Re-throw
            }
        }

        Exercise exercise = new Exercise();
        exercise.setName(exerciseDTO.getName());
        exercise.setDescription(exerciseDTO.getDescription());
        exercise.setImageUrl(imageUrl != null ? imageUrl : exerciseDTO.getImageUrl()); // Use uploaded URL or fallback to DTO if any
        exercise.setVideoTutorialUrl(videoTutorialUrl != null ? videoTutorialUrl : exerciseDTO.getVideoTutorialUrl()); // Use uploaded URL or fallback
        exercise.setDefaultDurationMinutes(exerciseDTO.getDefaultDurationMinutes());
        exercise.setCategories(exerciseDTO.getCategories());
        exercise.setDifficultyLevel(exerciseDTO.getDifficultyLevel());
        exercise.setEquipmentNeeded(exerciseDTO.getEquipmentNeeded());
        exercise.setTargetMuscleGroup(exerciseDTO.getTargetMuscleGroup());
        // Set other fields from ExerciseDTO if they exist in Exercise entity and DTO
        // e.g. exercise.setRecommendedRepetitions(exerciseDTO.getRecommendedRepetitions()); if you add it to DTO

        exercise.setCreatedBy(doctorId);
        exercise.setStatus("ACTIVE");
        exercise.setCreatedAt(LocalDateTime.now());
        exercise.setUpdatedAt(LocalDateTime.now()); // Also set updatedAt on creation

        logger.info("Attempting to save exercise: {}", exercise.getName());
        Exercise savedExercise = exerciseRepository.save(exercise);
        logger.info("Exercise saved successfully with ID: {}", savedExercise.getId());
        return savedExercise;
    }

    private void validateDoctor(String doctorId) {
        logger.debug("Validating doctor with ID: {}", doctorId);
        try {
            // Ensure USER-SERVICE is the correct service name registered with your discovery service (e.g., Eureka)
            UserDTO doctor = restTemplate.getForObject(
                    "http://USER-SERVICE/api/users/{Id}", // Assuming this is the correct endpoint in user-service
                    UserDTO.class,
                    doctorId
            );

            if (doctor == null) {
                logger.warn("Doctor validation failed: No user found for ID {}", doctorId);
                throw new EntityNotFoundException("Doctor details not found for ID: " + doctorId);
            }
            if (!"DOCTOR".equals(doctor.getRole())) { // Ensure getRole() method exists and role string matches
                logger.warn("Doctor validation failed: User {} is not a DOCTOR. Role: {}", doctorId, doctor.getRole());
                throw new EntityNotFoundException("User with ID: " + doctorId + " is not authorized (not a DOCTOR).");
            }
            logger.info("Doctor validation successful for ID: {}", doctorId);
        } catch (HttpClientErrorException.NotFound ex) {
            logger.warn("Doctor validation failed: User service returned 404 for ID {}", doctorId, ex);
            throw new EntityNotFoundException("Doctor not found with ID: " + doctorId + " (from user service).");
        } catch (ResourceAccessException ex) {
            logger.error("Doctor validation failed: Cannot access User service.", ex);
            throw new ServiceUnavailableException("User service is currently unavailable. Please try again later.");
        } catch (Exception ex) {
            logger.error("An unexpected error occurred during doctor validation for ID {}: {}", doctorId, ex.getMessage(), ex);
            throw new ServiceUnavailableException("An unexpected error occurred while validating doctor information.");
        }
    }

    public List<Exercise> getAllExercises() {
        logger.info("Fetching all exercises.");
        List<Exercise> exercises = exerciseRepository.findAll();
        logger.info("Found {} exercises.", exercises.size());
        return exercises;
    }
}