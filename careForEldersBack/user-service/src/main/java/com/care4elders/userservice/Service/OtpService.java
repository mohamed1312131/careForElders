package com.care4elders.userservice.Service;

import com.care4elders.userservice.Service.SmsService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {

    private final Map<String, String> otpStorage = new HashMap<>();

    public String generateAndSendOtp(String phoneNumber, SmsService smsService) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(phoneNumber, otp);
        smsService.sendSms(phoneNumber, "Your OTP is: " + otp);
        return otp;
    }

    public String generateAndSendEmailOtp(String email, EmailService emailService) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        otpStorage.put(email, otp);
        emailService.sendEmailOTP(email, "Your OTP is: " + otp);
        return otp;
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        return otp.equals(otpStorage.get(phoneNumber));
    }
}
