package com.care4elders.patientbill;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PatientBillApplicationTests {

    @Test
    void contextLoads() {
        // This test will verify that the Spring application context loads successfully
    }

    @Test
    void sampleTest() {
        // Example of a simple test
        int result = 1 + 1;
        assertThat(result).isEqualTo(2);
    }
}