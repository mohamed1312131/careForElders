package com.care4elders.userservice.controller;

import com.care4elders.userservice.entity.UserActivityLog;
import com.care4elders.userservice.Service.UserActivityLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class UserActivityLogController {

    private final UserActivityLogService service;

    public UserActivityLogController(UserActivityLogService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public List<UserActivityLog> getLogsByUser(@PathVariable String userId) {
        return service.getLogsForUser(userId);
    }

    @GetMapping
    public List<UserActivityLog> getAllLogs() {
        return service.getAllLogs();
    }
}
