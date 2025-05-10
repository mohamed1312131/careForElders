import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NutritionPlan } from '../models/nutrition-plan.model';

@Injectable({
  providedIn: 'root'
})
export class NutritionService {
  private apiUrl = 'http://localhost:8087/api/nutrition';

  constructor(private http: HttpClient) { }

  getAllPlans(): Observable<NutritionPlan[]> {
    return this.http.get<NutritionPlan[]>(this.apiUrl);
  }

  getPlanById(id: string): Observable<NutritionPlan> {
    return this.http.get<NutritionPlan>(`${this.apiUrl}/${id}`);
  }

  createPlan(plan: NutritionPlan): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(this.apiUrl, plan);
  }

  updatePlan(id: string, plan: NutritionPlan): Observable<NutritionPlan> {
    return this.http.put<NutritionPlan>(`${this.apiUrl}/${id}`, plan);
  }

  deletePlan(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  likePlan(id: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/like`, {});
  }

  dislikePlan(id: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/dislike`, {});
  }

  addComment(id: string, comment: string): Observable<NutritionPlan> {
    return this.http.post<NutritionPlan>(`${this.apiUrl}/${id}/comment`, comment);
  }
}
