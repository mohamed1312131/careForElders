import { Injectable } from "@angular/core"
import { HttpClient, HttpErrorResponse } from "@angular/common/http"
import { type Observable, throwError } from "rxjs"
import { catchError } from "rxjs/operators"
import { NutritionPlan } from "src/app/models/nutrition-plan.model";

export interface NutritionPlanRequest {
  userId: string;
  medicalConditions: string[];
  dietaryPreferences?: string;
  allergies?: string;
  targetCalories?: number;
  planDuration?: number;
  emailReminders?: boolean;
  snackTimes?: string[];
  userEmail?: string;
}

export interface NutritionPlanResponse {
  success: boolean
  nutritionPlan?: string
  message: string
  generatedAt: string
}

@Injectable({
  providedIn: "root",
})
export class NutritionService {
  // Use relative path for proxy compatibility
  private readonly apiUrl = "/api/nutrition-plans"

  constructor(private http: HttpClient) {}

  getAllPlans(): Observable<NutritionPlan[]> {
    // Fetch all plans with a large page size to ensure admin sees all
    return this.http.get<NutritionPlan[]>(`${this.apiUrl}?page=0&size=1000`)
      .pipe(catchError(this.handleError));
  }

  getPlanById(id: string): Observable<NutritionPlan> {
    return this.http.get<NutritionPlan>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  likePlan(id: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/like`, {})
      .pipe(catchError(this.handleError));
  }

  dislikePlan(id: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/dislike`, {})
      .pipe(catchError(this.handleError));
  }

  generateNutritionPlan(request: NutritionPlanRequest): Observable<NutritionPlanResponse> {
    return this.http
      .post<NutritionPlanResponse>(`${this.apiUrl}/generate`, request)
      .pipe(catchError(this.handleError))
  }

  deletePlan(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
      .pipe(catchError(this.handleError));
  }

  createPlan(plan: NutritionPlan): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(this.apiUrl, plan)
      .pipe(catchError(this.handleError));
  }

  updatePlan(id: string, plan: NutritionPlan): Observable<NutritionPlan> {
    return this.http.put<NutritionPlan>(`${this.apiUrl}/${id}`, plan)
      .pipe(catchError(this.handleError));
  }

  healthCheck(): Observable<string> {
    return this.http.get(`${this.apiUrl}/health`, { responseType: "text" }).pipe(catchError(this.handleError))
  }

  addComment(planId: string, comment: string): Observable<NutritionPlan> {
    // Backend expects an object with a 'comment' property
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${planId}/comments`, { comment })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = "An unknown error occurred"

    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`
    } else {
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`
      if (error.error && error.error.message) {
        errorMessage = error.error.message
      }
    }

    return throwError(() => errorMessage)
  }
}
