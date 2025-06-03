package com.care4elders.nutrition.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class HuggingFaceService {

    @Value("${huggingface.api.token}")
    private String apiToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateNutritionPlan(String userProfilePrompt) {
        // ✅ First try real AI generation
        String aiResponse = callHuggingFaceAPI(userProfilePrompt);

        if (aiResponse != null && !aiResponse.contains("Error")) {
            return aiResponse;
        }

        // ✅ Fallback to dynamic generation based on medical conditions
        return generateDynamicNutritionPlan(userProfilePrompt);
    }

    private String callHuggingFaceAPI(String prompt) {
        String apiUrl = "https://api-inference.huggingface.co/models/microsoft/DialoGPT-medium";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("inputs", prompt);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    Object.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                if (response.getBody() instanceof List) {
                    List<Map<String, Object>> responseList = (List<Map<String, Object>>) response.getBody();
                    if (!responseList.isEmpty()) {
                        Map<String, Object> output = responseList.get(0);
                        if (output.containsKey("generated_text")) {
                            return output.get("generated_text").toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("AI API call failed: {}", e.getMessage());
        }

        return null;
    }

    // ✅ DYNAMIC GENERATION based on medical conditions
    private String generateDynamicNutritionPlan(String medicalConditions) {
        log.info("Generating dynamic plan for conditions: {}", medicalConditions);

        // Parse medical conditions
        Set<String> conditions = parseConditions(medicalConditions);

        // Generate plan based on conditions
        StringBuilder plan = new StringBuilder();
        plan.append("🍽️ PERSONALIZED 30-DAY NUTRITION PLAN\n\n");
        plan.append("📋 Medical Considerations: ").append(medicalConditions).append("\n\n");

        // Dynamic meal planning based on conditions
        plan.append(generateWeeklyMeals(conditions));
        plan.append(generateDietaryGuidelines(conditions));
        plan.append(generateNutritionalTargets(conditions));
        plan.append(generateMedicalNotes(conditions));
        plan.append(generateReminders(conditions));

        return plan.toString();
    }

    private Set<String> parseConditions(String medicalConditions) {
        Set<String> conditions = new HashSet<>();
        String[] parts = medicalConditions.toLowerCase().split("[,;\\s]+");

        for (String part : parts) {
            part = part.trim();
            if (part.contains("diabet")) conditions.add("diabetes");
            if (part.contains("hypertension") || part.contains("high blood pressure")) conditions.add("hypertension");
            if (part.contains("heart") || part.contains("cardiac")) conditions.add("heart_disease");
            if (part.contains("kidney") || part.contains("renal")) conditions.add("kidney_disease");
            if (part.contains("osteoporosis") || part.contains("bone")) conditions.add("osteoporosis");
            if (part.contains("cholesterol")) conditions.add("high_cholesterol");
            if (part.contains("arthritis")) conditions.add("arthritis");
            if (part.contains("anemia")) conditions.add("anemia");
        }

        return conditions;
    }

    private String generateWeeklyMeals(Set<String> conditions) {
        StringBuilder meals = new StringBuilder();
        meals.append("📅 WEEK 1 MEAL PLAN:\n\n");

        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"};

        for (String day : days) {
            meals.append("🌅 ").append(day).append(":\n");
            meals.append(generateDayMeals(conditions));
            meals.append("\n");
        }

        return meals.toString();
    }

    private String generateDayMeals(Set<String> conditions) {
        StringBuilder dayMeals = new StringBuilder();

        // Breakfast options based on conditions
        dayMeals.append("• Breakfast: ").append(getBreakfast(conditions)).append("\n");
        dayMeals.append("• Lunch: ").append(getLunch(conditions)).append("\n");
        dayMeals.append("• Dinner: ").append(getDinner(conditions)).append("\n");
        dayMeals.append("• Snack: ").append(getSnack(conditions)).append("\n");

        return dayMeals.toString();
    }

    private String getBreakfast(Set<String> conditions) {
        List<String> options = new ArrayList<>();

        if (conditions.contains("diabetes")) {
            options.addAll(Arrays.asList(
                    "Steel-cut oatmeal with cinnamon and berries (low GI) (320 cal)",
                    "Greek yogurt with nuts and seeds (280 cal)",
                    "Whole grain toast with avocado and egg whites (350 cal)",
                    "Chia seed pudding with unsweetened almond milk (300 cal)"
            ));
        } else {
            options.addAll(Arrays.asList(
                    "Whole grain cereal with low-fat milk (360 cal)",
                    "Oatmeal with fresh fruits and honey (340 cal)",
                    "Scrambled eggs with whole grain toast (380 cal)"
            ));
        }

        if (conditions.contains("hypertension")) {
            options.replaceAll(meal -> meal.replace("toast", "low-sodium whole grain toast"));
        }

        return options.get(new Random().nextInt(options.size()));
    }

    private String getLunch(Set<String> conditions) {
        List<String> options = new ArrayList<>();

        if (conditions.contains("heart_disease") || conditions.contains("high_cholesterol")) {
            options.addAll(Arrays.asList(
                    "Grilled salmon salad with olive oil dressing (420 cal)",
                    "Quinoa bowl with roasted vegetables and chickpeas (400 cal)",
                    "Lentil soup with mixed green salad (380 cal)",
                    "Turkey and avocado wrap (whole grain) (410 cal)"
            ));
        } else {
            options.addAll(Arrays.asList(
                    "Grilled chicken Caesar salad (450 cal)",
                    "Vegetable stir-fry with brown rice (420 cal)",
                    "Bean and vegetable soup with bread (400 cal)"
            ));
        }

        if (conditions.contains("hypertension")) {
            options.replaceAll(meal -> meal.contains("soup") ?
                    meal.replace("soup", "low-sodium soup") : meal);
        }

        return options.get(new Random().nextInt(options.size()));
    }

    private String getDinner(Set<String> conditions) {
        List<String> options = new ArrayList<>();

        if (conditions.contains("diabetes")) {
            options.addAll(Arrays.asList(
                    "Baked cod with steamed broccoli and quinoa (440 cal)",
                    "Grilled chicken breast with roasted vegetables (460 cal)",
                    "Lean beef with sweet potato and green beans (480 cal)",
                    "Baked tofu with stir-fried vegetables (420 cal)"
            ));
        } else {
            options.addAll(Arrays.asList(
                    "Baked salmon with rice and vegetables (500 cal)",
                    "Grilled pork tenderloin with mashed potatoes (520 cal)",
                    "Chicken curry with brown rice (480 cal)"
            ));
        }

        if (conditions.contains("kidney_disease")) {
            options.replaceAll(meal -> meal.replace("potatoes", "cauliflower"));
        }

        return options.get(new Random().nextInt(options.size()));
    }

    private String getSnack(Set<String> conditions) {
        List<String> options = new ArrayList<>();

        if (conditions.contains("diabetes")) {
            options.addAll(Arrays.asList(
                    "Handful of almonds (160 cal)",
                    "Greek yogurt with berries (120 cal)",
                    "Apple slices with almond butter (140 cal)",
                    "Cottage cheese with cucumber (100 cal)"
            ));
        } else {
            options.addAll(Arrays.asList(
                    "Mixed nuts and dried fruits (180 cal)",
                    "Whole grain crackers with cheese (150 cal)",
                    "Fresh fruit salad (110 cal)"
            ));
        }

        return options.get(new Random().nextInt(options.size()));
    }

    private String generateDietaryGuidelines(Set<String> conditions) {
        StringBuilder guidelines = new StringBuilder();
        guidelines.append("📝 CONDITION-SPECIFIC DIETARY GUIDELINES:\n\n");

        if (conditions.contains("diabetes")) {
            guidelines.append("🩺 DIABETES MANAGEMENT:\n");
            guidelines.append("• Choose low glycemic index foods\n");
            guidelines.append("• Limit refined sugars and carbohydrates\n");
            guidelines.append("• Eat regular, balanced meals\n");
            guidelines.append("• Monitor carbohydrate intake\n\n");
        }

        if (conditions.contains("hypertension")) {
            guidelines.append("💓 HYPERTENSION MANAGEMENT:\n");
            guidelines.append("• Limit sodium to <1500mg daily\n");
            guidelines.append("• Increase potassium-rich foods (bananas, spinach)\n");
            guidelines.append("• Choose fresh over processed foods\n");
            guidelines.append("• Limit alcohol consumption\n\n");
        }

        if (conditions.contains("heart_disease")) {
            guidelines.append("❤️ HEART HEALTH:\n");
            guidelines.append("• Include omega-3 fatty acids (fish, walnuts)\n");
            guidelines.append("• Limit saturated and trans fats\n");
            guidelines.append("• Choose lean proteins\n");
            guidelines.append("• Increase fiber intake\n\n");
        }

        if (conditions.contains("osteoporosis")) {
            guidelines.append("🦴 BONE HEALTH:\n");
            guidelines.append("• Increase calcium-rich foods (dairy, leafy greens)\n");
            guidelines.append("• Ensure adequate vitamin D\n");
            guidelines.append("• Include magnesium sources\n");
            guidelines.append("• Limit caffeine and alcohol\n\n");
        }

        return guidelines.toString();
    }

    private String generateNutritionalTargets(Set<String> conditions) {
        StringBuilder targets = new StringBuilder();
        targets.append("💊 PERSONALIZED NUTRITIONAL TARGETS (Daily):\n");

        if (conditions.contains("diabetes")) {
            targets.append("• Calories: 1600-1800 (controlled portions)\n");
            targets.append("• Carbohydrates: 45-60g per meal\n");
            targets.append("• Fiber: 25-35g\n");
            targets.append("• Sugar: <25g\n");
        } else {
            targets.append("• Calories: 1800-2000\n");
            targets.append("• Carbohydrates: 225-325g\n");
            targets.append("• Sugar: <50g\n");
        }

        if (conditions.contains("hypertension")) {
            targets.append("• Sodium: <1500mg\n");
            targets.append("• Potassium: 3500-4700mg\n");
        } else {
            targets.append("• Sodium: <2300mg\n");
        }

        targets.append("• Protein: 80-100g\n");
        targets.append("• Healthy fats: 20-35% of calories\n\n");

        return targets.toString();
    }

    private String generateMedicalNotes(Set<String> conditions) {
        StringBuilder notes = new StringBuilder();
        notes.append("🏥 MEDICAL CONDITION SPECIFIC NOTES:\n");

        for (String condition : conditions) {
            switch (condition) {
                case "diabetes":
                    notes.append("• Monitor blood glucose levels before and after meals\n");
                    notes.append("• Take diabetes medications as prescribed\n");
                    break;
                case "hypertension":
                    notes.append("• Check blood pressure regularly\n");
                    notes.append("• Take blood pressure medications consistently\n");
                    break;
                case "heart_disease":
                    notes.append("• Monitor heart rate during physical activity\n");
                    notes.append("• Take cardiac medications as directed\n");
                    break;
                case "kidney_disease":
                    notes.append("• Limit protein and phosphorus intake\n");
                    notes.append("• Monitor fluid intake as advised\n");
                    break;
                case "osteoporosis":
                    notes.append("• Take calcium and vitamin D supplements if recommended\n");
                    notes.append("• Engage in weight-bearing exercises\n");
                    break;
            }
        }

        notes.append("• Stay hydrated throughout the day\n");
        notes.append("• Maintain regular meal times\n\n");

        return notes.toString();
    }

    private String generateReminders(Set<String> conditions) {
        return """
            ⚠️ IMPORTANT REMINDERS:
            • Consult with your healthcare provider before starting this plan
            • Monitor portion sizes and eating patterns
            • Take all medications as prescribed
            • Stay physically active as approved by your doctor
            • Keep a food and symptom diary
            • Report any adverse reactions immediately
            
            📞 Contact your nutritionist or healthcare provider for plan modifications or concerns.
            """;
    }
}