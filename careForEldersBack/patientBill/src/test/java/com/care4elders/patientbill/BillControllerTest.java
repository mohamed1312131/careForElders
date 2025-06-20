package com.care4elders.patientbill;

import com.care4elders.patientbill.controller.BillController;
import com.care4elders.patientbill.model.Bill;
import com.care4elders.patientbill.model.BillItem;
import com.care4elders.patientbill.service.BillService;
import com.care4elders.patientbill.service.PdfService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@WebMvcTest(BillController.class)
public class BillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BillService billService;

    @MockBean
    private PdfService pdfService;

    // Helper to create a sample bill
    private Bill getSampleBill() {
        Bill bill = new Bill();
        bill.setId("1");
        bill.setPatientName("John Doe");
        bill.setTotalAmount(BigDecimal.valueOf(100.0));
        //bill.setCreatedAt(LocalDateTime.now());
        return bill;
    }

    // Test: Create Bill
 @Autowired
private ObjectMapper objectMapper;
 @Test
public void testCreateBill_withObjectMapper() throws Exception {
    // Create a valid BillItem
    BillItem item = new BillItem();
    item.setDescription("Nursing Care");
    item.setQuantity(2);
    item.setUnitPrice(BigDecimal.valueOf(50.0));
    item.setServiceType(BillItem.ServiceType.PARA_MEDICAL_SERVICES);
    item.setServiceDate(java.time.LocalDate.now());
    item.setCreatedAt(LocalDateTime.now());
    item.setUpdatedAt(LocalDateTime.now());
    item.calculateAmount();

    // Create a valid Bill
    Bill bill = new Bill();
    bill.setPatientName("Jane Smith");
    bill.setPatientId("PAT123"); // <-- Important!
    bill.setTotalAmount(BigDecimal.valueOf(100.0));
    bill.setItems(List.of(item));

    // Set current bill date
    bill.setBillDate(new Date()); // <-- Optional but safe

    // Set due date (must be after billDate)
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(bill.getBillDate());
    calendar.add(Calendar.DAY_OF_MONTH, 30); // 30 days from now
    bill.setDueDate(calendar.getTime());

    bill.setStatus("PENDING");
    //bill.setCreatedAt(LocalDateTime.now());

    when(billService.createBill(any(Bill.class))).thenReturn(getSampleBill());

    mockMvc.perform(post("/api/bills")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bill)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.patientName").value("John Doe"))
            .andExpect(jsonPath("$.totalAmount").value(100.0))
            .andDo(print());
}
// Test: Update Bill

@Test
public void testUpdateBill() throws Exception {
    String billId = "1";

    // Create a valid BillItem
    BillItem item = new BillItem();
    item.setDescription("Physiotherapy");
    item.setQuantity(3);
    item.setUnitPrice(BigDecimal.valueOf(50.0));
    item.setServiceType(BillItem.ServiceType.PARA_MEDICAL_SERVICES);
    item.setServiceDate(java.time.LocalDate.now());
    item.calculateAmount();

    // Create the updated Bill
    Bill updateRequestBill = new Bill();
    updateRequestBill.setPatientName("Updated Name");
    updateRequestBill.setPatientId("PAT123"); // Required field
    updateRequestBill.setTotalAmount(BigDecimal.valueOf(150.0));

    // Set current date as billDate
    Date currentDate = new Date();
    updateRequestBill.setBillDate(currentDate);

    // Set dueDate (30 days from billDate)
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(currentDate);
    calendar.add(Calendar.DAY_OF_MONTH, 30);
    updateRequestBill.setDueDate(calendar.getTime());

    updateRequestBill.setStatus("PENDING");
    updateRequestBill.setItems(List.of(item)); // At least one item

    // Prepare the mocked response
    Bill updatedBillResponse = getSampleBill(); // This includes id: "1", patientName: "John Doe"
    updatedBillResponse.setPatientName("Updated Name");
    updatedBillResponse.setTotalAmount(BigDecimal.valueOf(150.0));
    updatedBillResponse.setItems(List.of(item));

    when(billService.updateBill(eq(billId), any(Bill.class))).thenReturn(updatedBillResponse);

    // Perform PUT request
    mockMvc.perform(put("/api/bills/{id}", billId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequestBill)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.patientName").value("Updated Name"))
            .andExpect(jsonPath("$.totalAmount").value(150.0))
            .andExpect(jsonPath("$.items[0].description").value("Physiotherapy"))
            .andDo(print()); // Print request/response for debugging
}

    // Test: Delete Bill
    @Test
    public void testDeleteBill() throws Exception {
        mockMvc.perform(delete("/api/bills/{id}", "1"))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    // Test: PDF Generation
    @Test
    public void testGeneratePdf() throws Exception {
        when(pdfService.generateInvoicePdf("1")).thenReturn(new ByteArrayInputStream("fake-pdf-content".getBytes()));

        mockMvc.perform(get("/api/bills/{id}/pdf", "1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=invoice_1.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andDo(print());
    }
}