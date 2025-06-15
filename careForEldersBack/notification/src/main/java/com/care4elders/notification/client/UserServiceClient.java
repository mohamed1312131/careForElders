package com.care4elders.notification.client;

import com.care4elders.notification.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", fallback = UserServiceClientFallback.class)
public interface UserServiceClient {
    @GetMapping("/users/all-ids")
    List<String> getAllUserIds();
    @GetMapping("/users/{id}/emergency-info")  // New endpoint in user-service
    UserDto getUserEmergencyInfo(@PathVariable String id);

}