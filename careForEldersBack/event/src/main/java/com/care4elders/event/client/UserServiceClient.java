package com.care4elders.event.client;

import com.care4elders.event.DTO.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.base-url}")
public interface UserServiceClient {
    @GetMapping("/users/{userId}")
    UserDTO getUserById(@PathVariable String userId);
}