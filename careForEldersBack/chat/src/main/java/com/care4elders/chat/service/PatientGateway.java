package com.care4elders.chat.service;
import com.care4elders.chat.DTO.PatientInfoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
public class PatientGateway {

    private final RestTemplate loadBalancedRestTemplate;

    public PatientInfoDTO getPatientInfo(String patientId) {
        return loadBalancedRestTemplate.getForObject(
                "http://USER-SERVICE/users/" + patientId,
                PatientInfoDTO.class
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
