package com.care4elders.chat.service;
import com.care4elders.chat.DTO.PatientInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PatientGateway {

    private final RestTemplate restTemplate;

    public PatientInfoDTO getPatientInfo(String patientId) {
        return restTemplate.getForObject(
                "http://USER-SERVICE/users/{id}",
                PatientInfoDTO.class,
                patientId
        );
    }

   /* public MedicalRecordDTO getMedicalRecord(String patientId) {
        return restTemplate.getForObject(
                "http://MEDICAL-RECORD-SERVICE/records/{id}",
                MedicalRecordDTO.class,
                patientId
        );
    }

    public PlanDTO getPlan(String patientId) {
        return restTemplate.getForObject(
                "http://PLAN-SERVICE/plans/{id}",
                PlanDTO.class,
                patientId
        );
    }

    public ExerciseDTO getExercise(String patientId) {
        return restTemplate.getForObject(
                "http://EXERCISE-SERVICE/exercises/{id}",
                ExerciseDTO.class,
                patientId
        );
    } */
}
