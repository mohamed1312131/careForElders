package com.care4elders.notification.client;

import com.care4elders.notification.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
class UserServiceClientFallback implements UserServiceClient {
    @Override
    public List<String> getAllUserIds() {  // Updated method
        System.out.println("Fallback triggered - returning empty list");
        return Collections.emptyList();
    }
    @Override
    public UserDto getUserEmergencyInfo(String id) {
        return null; // or a default UserDto if needed
    }
}