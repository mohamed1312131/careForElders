package com.care4elders.patientbill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableDiscoveryClient
public class PatientBillApplication {

    public static void main(String[] args) {
        log.info("Starting Patient Billing Application");
        SpringApplication.run(PatientBillApplication.class, args);
    }
}
