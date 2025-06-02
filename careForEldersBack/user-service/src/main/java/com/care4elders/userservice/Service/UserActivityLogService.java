package com.care4elders.userservice.Service;

import com.care4elders.userservice.entity.UserActivityLog;
import com.care4elders.userservice.repository.UserActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityLogService {

    private final UserActivityLogRepository repository;

    public UserActivityLogService(UserActivityLogRepository repository) {
        this.repository = repository;
    }

    public void log(String userId, String activity, String ipAddress) {
        UserActivityLog log = new UserActivityLog(userId, activity, ipAddress, LocalDateTime.now());
        repository.save(log);
    }

    public List<UserActivityLog> getLogsForUser(String userId) {
        return repository.findByUserId(userId);
    }

    public List<UserActivityLog> getAllLogs() {
        return repository.findAll();
    }
}
