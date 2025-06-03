import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NutritionPlan } from '../models/nutrition-plan.model';
import { MealSchedule } from '../models/meal-schedule.model';
//import { Comment } from '../models/comment.model'; // Assuming Comment model exists

@Injectable({
  providedIn: 'root'
})
export class NutritionService {
  private apiUrl = 'http://localhost:8087/api/nutrition-plans'; // Corrected API base URL

  constructor(private http: HttpClient) { }

  // ================================
  // STANDARD CRUD OPERATIONS
  // ================================

  /**
   * Create a new nutrition plan
   */
  createPlan(plan: NutritionPlan): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(this.apiUrl, plan);
  }

  /**
   * Get all nutrition plans with pagination and sorting
   */
  getAllPlans(page: number = 0, size: number = 10, sortBy: string = 'createdAt'): Observable<NutritionPlan[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy);
    return this.http.get<NutritionPlan[]>(this.apiUrl, { params });
  }

  /**
   * Get nutrition plan by ID
   */
  getPlanById(id: string): Observable<NutritionPlan> {
    return this.http.get<NutritionPlan>(`${this.apiUrl}/${id}`);
  }

  /**
   * Update nutrition plan (full update)
   */
  updatePlan(id: string, plan: NutritionPlan): Observable<NutritionPlan> {
    return this.http.put<NutritionPlan>(`${this.apiUrl}/${id}`, plan);
  }

  /**
   * Partially update nutrition plan
   */
  patchPlan(id: string, updates: Partial<NutritionPlan>): Observable<NutritionPlan> {
    return this.http.patch<NutritionPlan>(`${this.apiUrl}/${id}`, updates);
  }

  /**
   * Delete nutrition plan
   */
  deletePlan(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // ================================
  // QUERY OPERATIONS
  // ================================

  /**
   * Get nutrition plans by user ID with pagination
   */
  getPlansByUserId(userId: string, page: number = 0, size: number = 10): Observable<NutritionPlan[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<NutritionPlan[]>(`${this.apiUrl}/user/${userId}`, { params });
  }

  /**
   * Get plans by medical condition with pagination
   */
  getPlansByMedicalCondition(condition: string, page: number = 0, size: number = 10): Observable<NutritionPlan[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<NutritionPlan[]>(`${this.apiUrl}/condition/${condition}`, { params });
  }

  /**
   * Search plans by keyword with pagination
   */
  searchPlans(keyword: string, page: number = 0, size: number = 10): Observable<NutritionPlan[]> {
    let params = new HttpParams()
      .set('keyword', keyword)
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<NutritionPlan[]>(`${this.apiUrl}/search`, { params });
  }

  /**
   * Get statistics
   */
  getStats(): Observable<Map<string, number>> { // Assuming backend returns Map<String, Long>
    return this.http.get<Map<string, number>>(`${this.apiUrl}/stats`);
  }

  // ================================
  // AI-POWERED OPERATIONS
  // ================================

  /**
   * Generate monthly nutrition plan using AI
   */
  generateMonthlyPlan(userId: string, medicalConditions: string, userEmail: string): Observable<NutritionPlan> {
    let params = new HttpParams()
      .set('userId', userId)
      .set('medicalConditions', medicalConditions)
      .set('userEmail', userEmail);
    return this.http.post<NutritionPlan>(`${this.apiUrl}/generate`, {}, { params });
  }

  /**
   * Regenerate plan using AI
   */
  regeneratePlan(id: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/regenerate`, {});
  }

  // ================================
  // SOCIAL FEATURES
  // ================================

  /**
   * Like a nutrition plan
   */
  likePlan(id: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/like`, {});
  }

  /**
   * Dislike a nutrition plan
   */
  dislikePlan(id: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/dislike`, {});
  }

  /**
   * Add comment to nutrition plan
   */
  addComment(id: string, comment: string, userId: string = 'anonymous'): Observable<NutritionPlan> {
    // Backend expects Map<String, String>, sending as JSON object
    const commentData = { comment: comment, userId: userId };
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/comments`, commentData);
  }

  /**
   * Get comments for nutrition plan
   */
  getComments(id: string): Observable<Comment[]> {
    // Assuming backend returns List<Map<String, Object>> convertible to Comment[]
    return this.http.get<Comment[]>(`${this.apiUrl}/${id}/comments`);
  }

  /**
   * Delete comment from nutrition plan
   */
  deleteComment(id: string, commentId: string): Observable<NutritionPlan> {
    return this.http.delete<NutritionPlan>(`${this.apiUrl}/${id}/comments/${commentId}`);
  }

  // ================================
  // EMAIL FEATURES
  // ================================

  /**
   * Toggle email reminders for a plan
   */
  toggleEmailReminders(id: string, enabled: boolean): Observable<NutritionPlan> {
    const request = { enabled: enabled };
    return this.http.patch<NutritionPlan>(`${this.apiUrl}/${id}/email-reminders`, request);
  }

  /**
   * Update meal schedule for a plan
   */
  updateMealSchedule(id: string, schedule: MealSchedule): Observable<NutritionPlan> {
    return this.http.put<NutritionPlan>(`${this.apiUrl}/${id}/meal-schedule`, schedule);
  }
}

