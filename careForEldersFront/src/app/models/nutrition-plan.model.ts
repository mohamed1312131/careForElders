export interface NutritionPlan {
  id: string;
  userId: string;
  userEmail: string;
  medicalConditions: string;
  dietaryPreferences: string;
  allergies: string;
  aiGeneratedPlan?: string;
  content?: string;
  createdAt?: string;
  updatedAt?: string;
  lastReminderSent?: string;
  active: boolean;
  emailRemindersEnabled?: boolean;

  meal: string;
  description: string;
  calories: number;
  mealTime: string;
  notes: string;
  recommendedAgeGroup: string;
  pictureUrl: string;
  ingredients: string[];

  planDuration?: number;
  targetCalories?: number;
  mealSchedule?: any;

  comments: Array<{ id?: string; userId?: string; comment?: string; createdAt?: string }>; // Backend uses array of objects
  likes: number;
  dislikes: number;

  // For local image handling only (not sent to backend)
  localImage?: File | null;
  imagePreview?: string | ArrayBuffer | null;
}

