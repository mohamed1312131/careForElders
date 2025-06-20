package com.care4elders.nutrition;

import com.care4elders.nutrition.controller.NutritionPlanController;
import com.care4elders.nutrition.service.NutritionPlanService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.http.MediaType;
import static org.mockito.Mockito.*;

@WebMvcTest(NutritionPlanController.class)
public class NutritionPlanControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NutritionPlanService nutritionPlanService;

    @Test
    public void testGetAllNutritionPlans() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/nutrition-plans"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
