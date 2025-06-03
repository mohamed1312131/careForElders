import { MealSchedule } from "./meal-schedule.model";
import { Comment } from "./comment.model";

export interface NutritionPlan {
  id: string;
  userId: string;
  userEmail: string;
  medicalConditions: string;
  dietaryPreferences?: string; // Optional in DTO based on constructor
  allergies?: string; // Optional in DTO based on constructor
  aiGeneratedPlan?: string;
  content?: string; // For backward compatibility as noted in DTO
  createdAt: Date; // Assuming conversion from LocalDateTime
  updatedAt: Date; // Assuming conversion from LocalDateTime
  lastReminderSent?: Date; // Assuming conversion from LocalDateTime
  active: boolean;
  emailRemindersEnabled: boolean;
  likes: number;
  dislikes: number;
  comments: Comment[];
  mealSchedule?: MealSchedule;
  planDuration: number;
  targetCalories: number;
}
