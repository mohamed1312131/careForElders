package com.care4elders.patientbill;

import com.care4elders.patientbill.controller.PaymentSimulatorController;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.service.PaymentService;
import com.care4elders.patientbill.service.PaymentSimulatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentSimulatorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentService paymentService;

    @Mock
    private BillService billService;

    @Mock
    private PaymentSimulatorService paymentSimulatorService;

    @InjectMocks
    private PaymentSimulatorController paymentSimulatorController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentSimulatorController).build();
    }

    @Test
    void showPaymentSimulator_ShouldReturnPaymentDetails() throws Exception {
        // Arrange
        Payment payment = createTestPayment("1", "ONLINE");

        // Mock
        when(paymentService.findById("1")).thenReturn(Optional.of(payment));

        // Act & Assert
        mockMvc.perform(get("/api/payments/simulator/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment simulator for online payment"))
                .andExpect(jsonPath("$.payment.id").value("1"));

        // Verify
        verify(paymentService, times(1)).findById("1");
    }

    @Test
    void completePayment_ShouldCompletePayment() throws Exception {
        // Arrange
        Payment payment = createTestPayment("1", "ONLINE");
        Bill bill = createTestBill("bill1");

        // Mock
        when(paymentSimulatorService.simulateOnlinePayment("1")).thenReturn(true);
        when(paymentService.findById("1")).thenReturn(Optional.of(payment));
        when(billService.getBillById("bill1")).thenReturn(bill);

        // Act & Assert
        mockMvc.perform(post("/api/payments/simulator/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment processed successfully!"))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // Verify
        verify(paymentSimulatorService, times(1)).simulateOnlinePayment("1");
        verify(paymentService, times(1)).findById("1");
    }

    private Payment createTestPayment(String id, String method) {
        Payment payment = new Payment();
        payment.setId(id);
        payment.setBillId("bill1");
        payment.setAmount(100.0); // double
        payment.setPaymentMethod(method);
        payment.setStatus("COMPLETED");
        return payment;
    }

    private Bill createTestBill(String id) {
        Bill bill = new Bill();
        bill.setId(id);
        bill.setPatientId("patient1");
        bill.setTotalAmount(new BigDecimal("100.00"));
        return bill;
    }
}