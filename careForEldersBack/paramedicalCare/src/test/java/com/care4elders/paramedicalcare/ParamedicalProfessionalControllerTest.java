package com.care4elders.paramedicalcare;

import com.care4elders.paramedicalcare.controller.ParamedicalProfessionalController;
import com.care4elders.paramedicalcare.service.ParamedicalProfessionalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(ParamedicalProfessionalController.class)
public class ParamedicalProfessionalControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParamedicalProfessionalService paramedicalProfessionalService;

    @Test
    public void testGetAllParamedicalProfessionals() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/paramedical"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
