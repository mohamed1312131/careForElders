package com.care4elders.patientbill;

import com.care4elders.patientbill.controller.PaymentController;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.Payment;
import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.service.PaymentService;
import com.care4elders.patientbill.service.PaymentSimulatorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private BillService billService;

    @MockBean
    private PaymentSimulatorService paymentSimulatorService;

    // Helper to create a sample payment
    private Payment getSamplePayment() {
        Payment payment = new Payment();
        payment.setId("1");
        payment.setAmount(100.0); // double
        payment.setPaymentMethod("CASH");
        payment.setStatus("COMPLETED");
        payment.setBillId("BILL123");
        return payment;
    }

    // Helper to create a sample bill
    private Bill getSampleBill() {
        Bill bill = new Bill();
        bill.setId("BILL123");
        bill.setPatientName("John Doe");
        bill.setStatus("PAID");
        return bill;
    }

    // Test: GET /api/payments/{paymentId}
    @Test
    public void testGetPaymentById() throws Exception {
        String paymentId = "1";
        Payment payment = getSamplePayment();

        when(paymentService.findById(paymentId)).thenReturn(java.util.Optional.of(payment));

        mockMvc.perform(get("/api/payments/{paymentId}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andDo(print());
    }

    // Test: GET /api/payments
    @Test
    public void testGetAllPayments() throws Exception {
        List<Payment> payments = Arrays.asList(getSamplePayment());

        when(paymentService.findAll()).thenReturn(payments);

        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].amount").value(100.0))
                .andDo(print());
    }

    // Test: GET /api/payments?status=...
    @Test
    public void testGetPaymentsByStatus() throws Exception {
        List<Payment> payments = Arrays.asList(getSamplePayment());

        when(paymentService.findByStatus("COMPLETED")).thenReturn(payments);

        mockMvc.perform(get("/api/payments")
                .param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("COMPLETED"))
                .andDo(print());
    }

    // Test: GET /api/payments/bill/{billId}
    @Test
    public void testGetPaymentsByBillId() throws Exception {
        String billId = "BILL123";
        List<Payment> payments = Arrays.asList(getSamplePayment());

        when(paymentService.findByBillId(billId)).thenReturn(payments);

        mockMvc.perform(get("/api/payments/bill/{billId}", billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].billId").value(billId))
                .andDo(print());
    }

    // Test: POST /api/payments/process/{paymentId}?paymentMethod=CASH
    @Test
    public void testProcessCashPayment() throws Exception {
        String paymentId = "1";
        Payment payment = getSamplePayment();
        Bill bill = getSampleBill();

        when(paymentService.processPayment(paymentId, "CASH")).thenReturn(payment);
        when(billService.getBillById(payment.getBillId())).thenReturn(bill);

        mockMvc.perform(post("/api/payments/process/{paymentId}", paymentId)
                .param("paymentMethod", "CASH")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Payment completed successfully. Cash payment received."))
                .andExpect(jsonPath("$.bill.id").value("BILL123"))
                .andExpect(jsonPath("$.billStatus").value("PAID"))
                .andDo(print());
    }

    // Test: POST /api/payments/process/{paymentId}?paymentMethod=ONLINE - Success
    @Test
    public void testProcessOnlinePayment_Success() throws Exception {
        String paymentId = "1";
        Payment payment = getSamplePayment();
        Bill bill = getSampleBill();

        when(paymentService.processPayment(paymentId, "ONLINE")).thenReturn(payment);
        when(paymentSimulatorService.simulateOnlinePayment(paymentId)).thenReturn(true);
        when(billService.getBillById(payment.getBillId())).thenReturn(bill);

        mockMvc.perform(post("/api/payments/process/{paymentId}", paymentId)
                .param("paymentMethod", "ONLINE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Online payment processed successfully."))
                .andExpect(jsonPath("$.bill.id").value("BILL123"))
                .andExpect(jsonPath("$.billStatus").value("PAID"))
                .andDo(print());
    }

  
   
}