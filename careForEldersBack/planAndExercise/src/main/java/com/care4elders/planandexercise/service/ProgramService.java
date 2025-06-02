package com.care4elders.planandexercise.service;

import com.care4elders.planandexercise.DTO.*;
import com.care4elders.planandexercise.entity.*;
import com.care4elders.planandexercise.exception.*;
import com.care4elders.planandexercise.repository.ExerciseRepository;
import com.care4elders.planandexercise.repository.PatientProgramAssignmentRepository;
import com.care4elders.planandexercise.repository.ProgramDayRepository;
import com.care4elders.planandexercise.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ExerciseRepository exerciseRepository;
    private final ProgramDayRepository programDayRepository;
    private final RestTemplate restTemplate;
    private final PatientProgramAssignmentRepository assignmentRepository;

    // Create empty program
    @Transactional
    public Program createProgram(ProgramDTO programDTO, String doctorId, String imageUrl) {
        validateDoctor(doctorId);

        Program program = new Program();
        program.setName(programDTO.getName());
        program.setDescription(programDTO.getDescription());
        program.setDoctorId(doctorId);
        program.setProgramCategory(programDTO.getProgramCategory());
        program.setProgramImage(imageUrl);  // Set Cloudinary URL here
        program.setStatus("DRAFT");
        program = programRepository.save(program);

        // Create and link ProgramDays
        Program finalProgram = program;
        List<ProgramDay> days = programDTO.getDays().stream()
                .map(dayDTO -> createProgramDay(finalProgram.getId(), dayDTO))
                .collect(Collectors.toList());

        program.setProgramDayIds(days.stream()
                .map(ProgramDay::getId)
                .collect(Collectors.toList()));

        return programRepository.save(program);
    }

    private ProgramDay createProgramDay(String programId, ProgramDayCreationDTO dto) {
        ProgramDay day = new ProgramDay();
        day.setProgramId(programId);
        day.setDayNumber(dto.getDayNumber());
        day.setExerciseIds(dto.getExerciseIds());
        day.setRestDay(dto.isRestDay());
        day.setInstructions(dto.getInstructions());
        day.setWarmUpMinutes(dto.getWarmUpMinutes());
        day.setCoolDownMinutes(dto.getCoolDownMinutes());
        day.setNotesForPatient(dto.getNotesForPatient());

        // Calculate total duration
        if (!dto.isRestDay()) {
            List<Exercise> exercises = exerciseRepository.findAllById(dto.getExerciseIds());
            int exerciseDuration = exercises.stream()
                    .mapToInt(Exercise::getDefaultDurationMinutes)
                    .sum();
            day.setTotalDurationMinutes(exerciseDuration + dto.getWarmUpMinutes() + dto.getCoolDownMinutes());
        } else {
            day.setTotalDurationMinutes(dto.getWarmUpMinutes() + dto.getCoolDownMinutes());
        }

        return programDayRepository.save(day);
    }

    // Add day to program
    @Transactional
    public Program addDayToProgram(String programId, ProgramDayDTO dayDTO, String doctorId) {
        validateDoctor(doctorId);
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        // Ensure programDayIds is initialized (for existing programs)
        if (program.getProgramDayIds() == null) {
            program.setProgramDayIds(new ArrayList<>());
        }

        validateDayNumber(program, dayDTO.getDayNumber());
        ProgramDay newDay = createAndSaveProgramDay(programId, dayDTO);

        program.getProgramDayIds().add(newDay.getId());
        program.setUpdatedDate(LocalDateTime.now());

        return programRepository.save(program);
    }

    private ProgramDay createAndSaveProgramDay(String programId, ProgramDayDTO dto) {
        ProgramDay day = new ProgramDay();
        day.setProgramId(programId);
        day.setDayNumber(dto.getDayNumber());
        day.setRestDay(dto.isRestDay());

        if (dto.isRestDay()) {
            configureRestDay(day, dto);
        } else {
            configureExerciseDay(day, dto);
        }

        return programDayRepository.save(day);
    }

    private void configureRestDay(ProgramDay day, ProgramDayDTO dto) {
        day.setTotalDurationMinutes(dto.getWarmUpMinutes() + dto.getCoolDownMinutes());
        day.setInstructions(dto.getInstructions());
        day.setNotesForPatient(dto.getNotesForPatient());
        day.setNotesForDoctor(dto.getNotesForDoctor());
        day.setWarmUpMinutes(dto.getWarmUpMinutes());
        day.setCoolDownMinutes(dto.getCoolDownMinutes());
    }

    private void configureExerciseDay(ProgramDay day, ProgramDayDTO dto) {
        List<Exercise> exercises = exerciseRepository.findAllById(dto.getExerciseIds());
        validateExercisesFound(dto, exercises);

        int totalDuration = calculateTotalDuration(exercises, dto);
        day.setExerciseIds(dto.getExerciseIds());
        day.setTotalDurationMinutes(totalDuration);
        day.setInstructions(dto.getInstructions());
        day.setNotesForPatient(dto.getNotesForPatient());
        day.setNotesForDoctor(dto.getNotesForDoctor());
        day.setWarmUpMinutes(dto.getWarmUpMinutes());
        day.setCoolDownMinutes(dto.getCoolDownMinutes());
    }

    private void validateExercisesFound(ProgramDayDTO dto, List<Exercise> exercises) {
        if (exercises.size() != dto.getExerciseIds().size()) {
            List<String> foundIds = exercises.stream()
                    .map(Exercise::getId)
                    .toList();

            List<String> missingIds = dto.getExerciseIds().stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();

            throw new ExerciseNotFoundException("Missing exercise IDs: " + missingIds);
        }
    }

    private int calculateTotalDuration(List<Exercise> exercises, ProgramDayDTO dto) {
        return exercises.stream()
                .mapToInt(Exercise::getDefaultDurationMinutes)
                .sum() + dto.getWarmUpMinutes() + dto.getCoolDownMinutes();
    }

    private void validateDoctor(String doctorId) {
        try {
            UserDTO doctor = restTemplate.getForObject(
                    "http://user-service/users/{Id}",
                    UserDTO.class,
                    doctorId
            );

            if (doctor == null || !"DOCTOR".equals(doctor.getRole())) {
                throw new EntityNotFoundException("Doctor not found or invalid role");
            }
        } catch (HttpClientErrorException.NotFound ex) {
            throw new EntityNotFoundException("Doctor not found with ID: " + doctorId);
        } catch (ResourceAccessException ex) {
            throw new ServiceUnavailableException("User service unavailable");
        }
    }

    private void validateDayNumber(Program program, int dayNumber) {
        boolean dayExists = program.getDays().stream()
                .anyMatch(d -> d.getDayNumber() == dayNumber);

        if (dayExists) {
            throw new InvalidDaySequenceException("Day number " + dayNumber + " already exists");
        }
    }
    @Transactional
    public PatientProgramAssignment assignProgramToPatient(ProgramAssignmentDTO assignmentDTO, String doctorId) {
        // Validate doctor
       // validateDoctor(doctorId);

        // Get and validate program
        Program program = programRepository.findById(assignmentDTO.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        if (!program.getDoctorId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You don't own this program");
        }

        // Validate patient
        /*UserDTO patient = restTemplate.getForObject(
                "http://user-service/users/{Id}",
                UserDTO.class,
                assignmentDTO.getPatientId()
        );

        if (patient == null || !"NORMAL_USER".equals(patient.getRole())) {
            throw new EntityNotFoundException("Invalid patient ID");
        } */

        List<ProgramDay> days = programDayRepository.findByProgramIdOrderByDayNumberAsc(assignmentDTO.getProgramId());


        // Create assignment
        PatientProgramAssignment assignment = new PatientProgramAssignment();
        assignment.setProgramId(assignmentDTO.getProgramId());
        assignment.setPatientId(assignmentDTO.getPatientId());
        assignment.setAssignedDate(LocalDateTime.now());
        assignment.setStatus("ACTIVE");
        assignment.setCurrentDay(1);

        // Initialize day statuses
        Map<Integer, DayStatus> dayStatuses = new HashMap<>();
        for (ProgramDay day : days) {
            dayStatuses.put(day.getDayNumber(), new DayStatus(
                    false,
                    null,
                    0,
                    null,
                    null,
                    "",
                    List.of(),
                    0,
                    ""
            ));
        }
        assignment.setDayStatuses(dayStatuses);

        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void unassignPatientFromProgram(String programId, String patientId, String doctorId) {
        // Validate ownership
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        if (!program.getDoctorId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You don't own this program");
        }

        // Find the assignment
        PatientProgramAssignment assignment = assignmentRepository
                .findByProgramIdAndPatientId(programId, patientId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        // Delete the assignment
        assignmentRepository.delete(assignment);
    }

    @Transactional
    public PatientProgramAssignment completeDay(String assignmentId, DayCompletionDTO completionDTO, String patientId) {
        PatientProgramAssignment assignment = getValidAssignment(assignmentId, patientId);

        // Validate current day
        if (assignment.getCurrentDay() != completionDTO.getDayNumber()) {
            throw new InvalidProgramStateException("Cannot complete unstarted day");
        }

        // Validate previous days
        for (int i = 1; i < completionDTO.getDayNumber(); i++) {
            if (!assignment.getDayStatuses().get(i).isCompleted()) {
                throw new InvalidProgramStateException("Previous days not completed");
            }
        }

        DayStatus dayStatus = assignment.getDayStatuses().get(completionDTO.getDayNumber());
        dayStatus.setCompleted(true);
        dayStatus.setCompletionDate(LocalDateTime.now());
        dayStatus.setActualDurationMinutes(completionDTO.getActualDurationMinutes());
        dayStatus.setPatientNotes(completionDTO.getPatientNotes());
        dayStatus.setPerceivedDifficulty(completionDTO.getPerceivedDifficulty());

        // Update completion percentage
        long completedDays = assignment.getDayStatuses().values().stream()
                .filter(DayStatus::isCompleted)
                .count();
        double percentage = (completedDays * 100.0) / assignment.getDayStatuses().size();
        assignment.setCompletionPercentage(Math.round(percentage * 100.0) / 100.0);

        // Update status if all days completed
        if (completedDays == assignment.getDayStatuses().size()) {
            assignment.setStatus("COMPLETED");
            assignment.setActualEndDate(LocalDateTime.now());
        }

        assignment.setLastActivityDate(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public PatientProgramAssignment startDay(String assignmentId, int dayNumber, String patientId) {
        PatientProgramAssignment assignment = getValidAssignment(assignmentId, patientId);

        // Validate that this is the next day to be started
        if (dayNumber != assignment.getCurrentDay() + 1) {
            throw new InvalidProgramStateException("Can only start the next sequential day");
        }

        // Validate previous days are completed (except for day 1)
        if (dayNumber > 1) {
            for (int i = 1; i < dayNumber; i++) {
                DayStatus prevDayStatus = assignment.getDayStatuses().get(i);
                if (prevDayStatus == null || !prevDayStatus.isCompleted()) {
                    throw new InvalidProgramStateException("Previous days must be completed first");
                }
            }
        }

        // Update current day and day status
        assignment.setCurrentDay(dayNumber);

        DayStatus dayStatus = assignment.getDayStatuses().get(dayNumber);
        if (dayStatus == null) {
            dayStatus = new DayStatus(
                    false, null, 0, null, null, "",
                    List.of(), 0, ""
            );
            assignment.getDayStatuses().put(dayNumber, dayStatus);
        }

        dayStatus.setActualStartDateTime(LocalDateTime.now());
        assignment.setLastActivityDate(LocalDateTime.now());
        return assignmentRepository.save(assignment);
    }

    private PatientProgramAssignment getValidAssignment(String assignmentId, String patientId) {
        PatientProgramAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        if (!assignment.getPatientId().equals(patientId)) {
            throw new UnauthorizedAccessException("This assignment doesn't belong to you");
        }

        if (!"ACTIVE".equals(assignment.getStatus())) {
            throw new InvalidProgramStateException("Program is not active");
        }

        return assignment;
    }
    public List<PatientProgramDTO> getProgramsByPatientId(String patientId) {
        // Verify patient exists
        /*UserDTO patient = restTemplate.getForObject(
                "http://user-service/users/{Id}",
                UserDTO.class,
                patientId
        );

        if (patient == null || !"NORMAL_USER".equals(patient.getRole())) {
            throw new EntityNotFoundException("Patient not found");
        } */

        // Get all assignments for the patient
        List<PatientProgramAssignment> assignments = assignmentRepository.findByPatientId(patientId);

        return assignments.stream()
                .map(assignment -> {
                    Program program = programRepository.findById(assignment.getProgramId())
                            .orElseThrow(() -> new EntityNotFoundException("Program not found"));
                    return new PatientProgramDTO(program, assignment);
                })
                .collect(Collectors.toList());
    }

    public ProgramDetailsWithProgressDTO getProgramDetailsWithProgress(String programId, String patientId) {
        // Get program and assignment
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));
        PatientProgramAssignment assignment = assignmentRepository.findByProgramIdAndPatientId(programId, patientId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        // Get days sorted by dayNumber
        List<ProgramDay> days = programDayRepository.findByProgramIdOrderByDayNumberAsc(programId);

        // Get all exercise IDs
        List<String> exerciseIds = days.stream()
                .flatMap(day -> day.getExerciseIds().stream())
                .collect(Collectors.toList());

        // Get exercises map
        Map<String, Exercise> exercisesMap = exerciseRepository.findAllById(exerciseIds).stream()
                .collect(Collectors.toMap(Exercise::getId, Function.identity()));

        // Build day details
        List<DayDetailsDTO> dayDetails = days.stream()
                .map(day -> mapToDayDetails(day, exercisesMap, assignment))
                .collect(Collectors.toList());

        return ProgramDetailsWithProgressDTO.builder()
                .programId(programId)
                .assignmentId(assignment.getId())
                .programName(program.getName())
                .description(program.getDescription())
                .completionPercentage(assignment.getCompletionPercentage())
                .currentDay(assignment.getCurrentDay())
                .days(dayDetails)
                .build();
    }

    private DayDetailsDTO mapToDayDetails(ProgramDay day, Map<String, Exercise> exercisesMap,
                                          PatientProgramAssignment assignment) {
        DayDetailsDTO dto = new DayDetailsDTO();
        dto.setDayNumber(day.getDayNumber());
        dto.setTotalDuration(day.getTotalDurationMinutes());

        // Get current day status
        DayStatus currentDayStatus = assignment.getDayStatuses().getOrDefault(
                day.getDayNumber(),
                new DayStatus() // Default uncompleted status
        );

        // Check previous day completion safely
        boolean isLocked = false;
        if (day.getDayNumber() > 1) {
            DayStatus previousDayStatus = assignment.getDayStatuses().get(day.getDayNumber() - 1);
            isLocked = previousDayStatus == null || !previousDayStatus.isCompleted();
        }

        dto.setLocked(isLocked);
        dto.setCompleted(currentDayStatus.isCompleted());
        dto.setCompletionDate(currentDayStatus.getCompletionDate());

        // Map exercises
        List<ExerciseDTO> exercises = day.getExerciseIds().stream()
                .map(exercisesMap::get)
                .filter(Objects::nonNull)
                .map(this::mapToExerciseDTO)
                .collect(Collectors.toList());
        dto.setExercises(exercises);

        // Determine status
        if (dto.isCompleted()) {
            dto.setStatus("COMPLETED");
        } else if (assignment.getCurrentDay() == day.getDayNumber()) {
            dto.setStatus("IN_PROGRESS");
        } else {
            dto.setStatus("PENDING");
        }

        return dto;
    }

    private ExerciseDTO mapToExerciseDTO(Exercise exercise) {
        return ExerciseDTO.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .imageUrl(exercise.getImageUrl())
                .videoUrl(exercise.getVideoTutorialUrl())
                .durationMinutes(exercise.getDefaultDurationMinutes())
                .build();
    }

    public DayExercisesDTO getDayExercises(String assignmentId, int dayNumber, String patientId) {
        PatientProgramAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        if (!assignment.getPatientId().equals(patientId)) {
            throw new UnauthorizedAccessException("Access denied");
        }

        Program program = programRepository.findById(assignment.getProgramId())
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        ProgramDay day = programDayRepository.findByProgramIdAndDayNumber(program.getId(), dayNumber)
                .orElseThrow(() -> new EntityNotFoundException("Day not found"));

        // Check if day is locked
        boolean isLocked = dayNumber > 1 && !isPreviousDayCompleted(assignment, dayNumber);

        List<Exercise> exercises = exerciseRepository.findAllById(day.getExerciseIds());

        return DayExercisesDTO.builder()
                .dayNumber(dayNumber)
                .exercises(exercises.stream().map(this::mapToExerciseDTO).collect(Collectors.toList()))
                .totalDuration(day.getTotalDurationMinutes())
                .status(getDayStatus(assignment, dayNumber))
                .startTime(getDayStartTime(assignment, dayNumber))
                .locked(isLocked)
                .build();
    }

    private boolean isPreviousDayCompleted(PatientProgramAssignment assignment, int currentDay) {
        if (currentDay == 1) return true;
        DayStatus prevDay = assignment.getDayStatuses().get(currentDay - 1);
        return prevDay != null && prevDay.isCompleted();
    }

    private String getDayStatus(PatientProgramAssignment assignment, int dayNumber) {
        DayStatus status = assignment.getDayStatuses().get(dayNumber);
        if (status == null) return "NOT_STARTED";
        if (status.isCompleted()) return "COMPLETED";
        return assignment.getCurrentDay() == dayNumber ? "IN_PROGRESS" : "NOT_STARTED";
    }

    private LocalDateTime getDayStartTime(PatientProgramAssignment assignment, int dayNumber) {
        DayStatus status = assignment.getDayStatuses().get(dayNumber);
        return status != null ? status.getActualStartDateTime() : null;
    }

    public List<ProgramListDTO> getAllPrograms() {
        List<Program> programs = programRepository.findAll();

        return programs.stream().map(program -> {
            ProgramListDTO dto = new ProgramListDTO();
            dto.setId(program.getId());
            dto.setName(program.getName());
            dto.setDescription(program.getDescription());
            dto.setProgramCategory(program.getProgramCategory());
            dto.setStatus(program.getStatus());
            dto.setDurationWeeks(program.getDurationWeeks());
            dto.setCreatedDate(program.getCreatedDate());
            dto.setCreatedBy(program.getCreatedBy());

            // Get aggregated data
            dto.setNumberOfDays(Math.toIntExact(programDayRepository.countByProgramId(program.getId())));
            dto.setNumberOfExercises(Math.toIntExact(programDayRepository.countExercisesByProgramId(program.getId())));
            dto.setNumberOfPatients(Math.toIntExact(assignmentRepository.countByProgramId(program.getId())));


            return dto;
        }).collect(Collectors.toList());
    }

    public ProgramDetailsDTO getProgramDetails(String programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        List<ProgramDay> days = programDayRepository.findByProgramIdOrderByDayNumberAsc(programId);

        // Get all exercise IDs across all days
        List<String> exerciseIds = days.stream()
                .flatMap(day -> day.getExerciseIds().stream())
                .collect(Collectors.toList());

        Map<String, Exercise> exercisesMap = exerciseRepository.findAllById(exerciseIds)
                .stream()
                .collect(Collectors.toMap(Exercise::getId, Function.identity()));

        List<ProgramDayDetailsDTO> dayDtos = days.stream()
                .map(day -> mapToDayDetails(day, exercisesMap))
                .collect(Collectors.toList());

        return ProgramDetailsDTO.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .programCategory(program.getProgramCategory())
                .programImage(program.getProgramImage())
                .status(program.getStatus())
                .durationWeeks(program.getDurationWeeks())
                .createdDate(program.getCreatedDate())
                .updatedDate(program.getUpdatedDate())
                .createdBy(program.getCreatedBy())
                .days(dayDtos)
                .build();
    }

    private ProgramDayDetailsDTO mapToDayDetails(ProgramDay day, Map<String, Exercise> exercisesMap) {
        return ProgramDayDetailsDTO.builder()
                .id(day.getId())
                .dayNumber(day.getDayNumber())
                .restDay(day.isRestDay())
                .totalDurationMinutes(day.getTotalDurationMinutes())
                .instructions(day.getInstructions())
                .warmUpMinutes(day.getWarmUpMinutes())
                .coolDownMinutes(day.getCoolDownMinutes())
                .notesForPatient(day.getNotesForPatient())
                .notesForDoctor(day.getNotesForDoctor())
                .exercises(day.getExerciseIds().stream()
                        .map(exercisesMap::get)
                        .filter(Objects::nonNull)
                        .map(this::mapToExerciseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private ExerciseDTO mapToExerciseDto(Exercise exercise) {
        return ExerciseDTO.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .imageUrl(exercise.getImageUrl())
                .videoTutorialUrl(exercise.getVideoTutorialUrl())
                .defaultDurationMinutes(exercise.getDefaultDurationMinutes())
                .categories(exercise.getCategories())
                .difficultyLevel(exercise.getDifficultyLevel())
                .equipmentNeeded(exercise.getEquipmentNeeded())
                .targetMuscleGroup(exercise.getTargetMuscleGroup())
                .build();
    }

    public List<PatientAssignmentDTO> getProgramPatients(String programId) {
        List<PatientProgramAssignment> assignments = assignmentRepository.findByProgramId(programId);

        return assignments.stream()
                .map(assignment -> {
                    UserDTO patient = getUserDetails(assignment.getPatientId());
                    return PatientAssignmentDTO.builder()
                            .patientId(assignment.getPatientId())
                            .name(patient.getFirstName() + " " + patient.getLastName())
                            .email(patient.getEmail())
                            .assignedDate(assignment.getAssignedDate())
                            .completionPercentage(assignment.getCompletionPercentage())
                            .status(assignment.getStatus())
                            .build();
                })
                .collect(Collectors.toList());
    }

    private UserDTO getUserDetails(String patientId) {
        try {
            return restTemplate.getForObject(
                    "http://user-service/users/{Id}",
                    UserDTO.class,
                    patientId
            );
        } catch (Exception e) {
            // Handle error or return default
            return new UserDTO("Unknown", "User", "N/A");
        }

    }
    @Transactional
    public ProgramDay updateProgramDay(String programId, String dayId, ProgramDayDTO dayDTO, String doctorId) {
        //validateDoctor(doctorId);
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        if (!program.getDoctorId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You don't own this program");
        }

        ProgramDay existingDay = programDayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("Day not found"));

        // Update day properties
        existingDay.setRestDay(dayDTO.isRestDay());
        existingDay.setInstructions(dayDTO.getInstructions());
        existingDay.setNotesForPatient(dayDTO.getNotesForPatient());
        existingDay.setWarmUpMinutes(dayDTO.getWarmUpMinutes());
        existingDay.setCoolDownMinutes(dayDTO.getCoolDownMinutes());

        if (!dayDTO.isRestDay()) {
            List<Exercise> exercises = exerciseRepository.findAllById(dayDTO.getExerciseIds());
            validateExercisesFound(dayDTO, exercises);
            existingDay.setExerciseIds(dayDTO.getExerciseIds());
            existingDay.setTotalDurationMinutes(calculateTotalDuration(exercises, dayDTO));
        } else {
            existingDay.setExerciseIds(Collections.emptyList());
            existingDay.setTotalDurationMinutes(dayDTO.getWarmUpMinutes() + dayDTO.getCoolDownMinutes());
        }

        return programDayRepository.save(existingDay);
    }

    @Transactional
    public void deleteProgramDay(String programId, String dayId, String doctorId) {
        //validateDoctor(doctorId);
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        if (!program.getDoctorId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You don't own this program");
        }

        ProgramDay day = programDayRepository.findById(dayId)
                .orElseThrow(() -> new EntityNotFoundException("Day not found"));

        // Remove from program's day list
        program.getProgramDayIds().remove(dayId);
        programRepository.save(program);

        programDayRepository.delete(day);
    }

    @Transactional
    public Program updateProgram(String programId, ProgramDTO programDTO, String doctorId) {
        validateDoctor(doctorId);

        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        if (!program.getDoctorId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You don't own this program");
        }

        // Update fields from DTO
        program.setName(programDTO.getName());
        program.setDescription(programDTO.getDescription());
        program.setProgramCategory(programDTO.getProgramCategory());
        program.setProgramImage(programDTO.getProgramImage());
        program.setStatus(programDTO.getStatus()); // ENUM: validated already
        program.setDurationWeeks(programDTO.getDurationWeeks());
        program.setUpdatedDate(LocalDateTime.now());

        return programRepository.save(program);
    }

    @Transactional
    public void deleteProgram(String programId, String doctorId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new EntityNotFoundException("Program not found"));

        if (!program.getDoctorId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You are not authorized to delete this program");
        }

        // Step 1: Delete all patient assignments
        assignmentRepository.deleteByProgramId(programId);

        // Step 2: Delete all ProgramDays
        List<String> dayIds = program.getProgramDayIds();
        if (dayIds != null && !dayIds.isEmpty()) {
            programDayRepository.deleteAllByIdIn(dayIds);
        }

        // Step 3: Delete the Program
        programRepository.delete(program);
    }


}




