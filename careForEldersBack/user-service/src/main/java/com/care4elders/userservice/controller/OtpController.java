package com.care4elders.userservice.controller;

import com.care4elders.userservice.Service.EmailService;
import com.care4elders.userservice.Service.OtpService;
import com.care4elders.userservice.Service.SmsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class OtpController {

    private final OtpService otpService;
    private final SmsService smsService;
    private final EmailService emailService;
    public OtpController(OtpService otpService, SmsService smsService, EmailService emailService) {
        this.otpService = otpService;
        this.smsService = smsService;
        this.emailService = emailService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        otpService.generateAndSendOtp(phone, smsService);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phone = request.get("phone");
        String otp = request.get("otp");

        String target = email != null ? email : phone;
        if (target == null || otp == null) {
            return ResponseEntity.badRequest().body("Missing fields");
        }

        boolean valid = otpService.verifyOtp(target, otp);
        if (!valid) {
            return ResponseEntity.status(401).body("Invalid OTP");
        }

        //otpService.clearOtp(target); // optional cleanup
        return ResponseEntity.ok(Map.of("message", "OTP verified"));
    }


    @PostMapping("/send-email-otp")
    public ResponseEntity<?> sendEmailOtp(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        otpService.generateAndSendEmailOtp(email, emailService);
        return ResponseEntity.ok(Map.of("message", "Email OTP sent"));
    }
}
