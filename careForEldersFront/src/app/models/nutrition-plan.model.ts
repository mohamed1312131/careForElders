export interface NutritionPlan {
  id: string;
  meal: string;
  description: string;
  calories: number;
  pictureUrl: string;
  mealTime: string;
  notes: string;
  recommendedAgeGroup: string;
  // New fields
  ingredients: string[];
  comments: string[];
  likes: number;
  dislikes: number;

  // For local image handling
  localImage?: File | null;
  imagePreview?: string | ArrayBuffer | null;
}
