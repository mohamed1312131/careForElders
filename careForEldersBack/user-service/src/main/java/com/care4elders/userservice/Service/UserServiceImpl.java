package com.care4elders.userservice.Service;

import com.care4elders.userservice.dto.UpdateUserRequest;
import com.care4elders.userservice.dto.UserRequest;
import com.care4elders.userservice.dto.UserResponse;
import com.care4elders.userservice.entity.User;
import com.care4elders.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.care4elders.userservice.Config.CloudinaryConfig;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           EmailService emailService,
                           CloudinaryService cloudinaryService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
    }

    private UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setRole(user.getRole());
        response.setBirthDate(user.getBirthDate());
        response.setProfileImage(user.getProfileImage());
        response.setSpecialization(user.getSpecialization());
        response.setTwoFactorEnabled(user.isTwoFactorEnabled());

        // Add emergency contact fields
        response.setEmergencyContactName(user.getEmergencyContactName());
        response.setEmergencyContactPhone(user.getEmergencyContactPhone());
        response.setEmergencyContactEmail(user.getEmergencyContactEmail());

        return response;
    }

    @Override
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setBirthDate(request.getBirthDate());
        user.setProfileImage(request.getProfileImage());
        user.setEnabled(false);

        // Add emergency contact info
        user.setEmergencyContactName(request.getEmergencyContactName());
        user.setEmergencyContactPhone(request.getEmergencyContactPhone());
        user.setEmergencyContactEmail(request.getEmergencyContactEmail());

        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return null;
        }

        // Existing fields
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getBirthDate() != null) user.setBirthDate(request.getBirthDate());
        if (request.getProfileImage() != null) user.setProfileImage(request.getProfileImage());
        if (request.getRole() != null) user.setRole(request.getRole());
        if (request.getTwoFactorEnabled() != null) {
            user.setTwoFactorEnabled(request.getTwoFactorEnabled());
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // Add emergency contact updates
        if (request.getEmergencyContactName() != null) {
            user.setEmergencyContactName(request.getEmergencyContactName());
        }
        if (request.getEmergencyContactPhone() != null) {
            user.setEmergencyContactPhone(request.getEmergencyContactPhone());
        }
        if (request.getEmergencyContactEmail() != null) {
            user.setEmergencyContactEmail(request.getEmergencyContactEmail());
        }

        User updatedUser = userRepository.save(user);
        return mapToResponse(updatedUser);
    }



    @Override
    public UserResponse updateProfileImage(String userId, String imageUrl) {
        return userRepository.findById(userId)
                .map(user -> {
                    // First delete old image if exists
                    if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                        try {
                            // Extract public_id from URL if needed or store it separately
                            // cloudinaryService.deleteImage(publicId);
                        } catch (Exception e) {
                            // Log warning but continue with new image upload
                            System.err.println("Failed to delete old image: " + e.getMessage());
                        }
                    }

                    user.setProfileImage(imageUrl);
                    User savedUser = userRepository.save(user);
                    return mapToResponse(savedUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public UserResponse getUserById(String id) {
        return userRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<UserResponse> getAllDoctors() {
        return userRepository.getUsersByRole("DOCTOR").stream()
                .map(this::mapToResponse)
                .toList();
    }
    @Override
    public List<UserResponse> getUsersByRole(String role) {
        return userRepository.getUsersByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    @Override
    public List<User> getAllUsersN() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users;
    }
}
