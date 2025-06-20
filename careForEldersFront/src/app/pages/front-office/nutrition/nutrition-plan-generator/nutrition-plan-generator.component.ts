import { Component, type OnInit } from "@angular/core"
import { FormBuilder, FormGroup, Validators, FormArray } from "@angular/forms"
import { MatSnackBar } from "@angular/material/snack-bar"
import { NutritionService, NutritionPlanRequest } from "../services/nutrition.services"

@Component({
  selector: "app-nutrition-plan-generator",
  templateUrl: "./nutrition-plan-generator.component.html",
  styleUrls: ["./nutrition-plan-generator.component.scss"],
})
export class NutritionPlanGeneratorComponent implements OnInit {
  nutritionForm: FormGroup
  isLoading = false
  nutritionPlan = ""

  medicalConditionOptions = [
    "Diabetes",
    "Hypertension",
    "Heart Disease",
    "Kidney Disease",
    "Osteoporosis",
    "High Cholesterol",
    "Arthritis",
    "Anemia",
  ]

  constructor(
    private fb: FormBuilder,
    private nutritionService: NutritionService,
    private snackBar: MatSnackBar,
  ) {
    this.nutritionForm = this.createForm()
  }

  ngOnInit(): void {
    this.checkServiceHealth()
  }

  private createForm(): FormGroup {
    return this.fb.group({
      userProfile: ["", [Validators.required, Validators.minLength(10)]],
      medicalConditions: this.fb.array([], [Validators.required]),
      dietaryPreferences: ["", []],
      allergies: ["", []],
      targetCalories: [null, [Validators.min(0)]],
      planDuration: [null, [Validators.min(1)]],
      emailReminders: [false],
      snackTimes: this.fb.array([])
    })
  }

  get medicalConditionsArray(): FormArray {
    return this.nutritionForm.get("medicalConditions") as FormArray
  }

  get snackTimesArray(): FormArray {
    return this.nutritionForm.get("snackTimes") as FormArray;
  }

  addSnackTime(): void {
    this.snackTimesArray.push(this.fb.control(''));
  }

  removeSnackTime(index: number): void {
    this.snackTimesArray.removeAt(index);
  }

  onMedicalConditionChange(condition: string, isChecked: boolean): void {
    if (isChecked) {
      this.medicalConditionsArray.push(this.fb.control(condition))
    } else {
      const index = this.medicalConditionsArray.controls.findIndex((control) => control.value === condition)
      if (index >= 0) {
        this.medicalConditionsArray.removeAt(index)
      }
    }
  }

  isConditionSelected(condition: string): boolean {
    return this.medicalConditionsArray.controls.some((control) => control.value === condition)
  }

  onSubmit(): void {
    if (this.nutritionForm.invalid) {
      this.nutritionForm.markAllAsTouched()
      return
    }
    this.isLoading = true
    const formValue = this.nutritionForm.value
    // Build the DTO for backend submission
    const request: NutritionPlanRequest = {
      userId: formValue.userProfile,
      medicalConditions: (this.nutritionForm.get('medicalConditions') as FormArray).getRawValue(),
      dietaryPreferences: formValue.dietaryPreferences,
      allergies: formValue.allergies,
      targetCalories: formValue.targetCalories,
      planDuration: formValue.planDuration,
      emailReminders: formValue.emailReminders,
      snackTimes: (this.nutritionForm.get('snackTimes') as FormArray).getRawValue(),
    };
    this.nutritionService.generateNutritionPlan(request).subscribe({
      next: (response) => {
        this.isLoading = false
        if (response.success && response.nutritionPlan) {
          this.nutritionPlan = response.nutritionPlan
          this.showSuccess("Nutrition plan generated successfully using Hugging Face AI!")
        } else {
          this.showError(response.message || "Failed to generate nutrition plan")
        }
      },
      error: (error) => {
        this.isLoading = false
        this.showError(error || "An error occurred while generating the nutrition plan")
      },
    })
  }

  downloadPlan(): void {
    if (!this.nutritionPlan) return

    const blob = new Blob([this.nutritionPlan], { type: "text/plain" })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement("a")
    link.href = url
    link.download = "huggingface-nutrition-plan.txt"
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
  }

  clearForm(): void {
    this.nutritionForm.reset()
    this.medicalConditionsArray.clear()
    this.nutritionPlan = ""
  }

  private checkServiceHealth(): void {
    this.nutritionService.healthCheck().subscribe({
      next: (response) => {
        console.log("Service health check:", response)
        this.showSuccess("Connected to Hugging Face nutrition service")
      },
      error: (error) => {
        console.warn("Service health check failed:", error)
        this.showError("Warning: Unable to connect to nutrition service")
      },
    })
  }

  private markFormGroupTouched(): void {
    Object.keys(this.nutritionForm.controls).forEach((key) => {
      const control = this.nutritionForm.get(key)
      control?.markAsTouched()
    })
  }

  private showSuccess(message: string): void {
    this.snackBar.open(message, "Close", {
      duration: 5000,
      panelClass: ["success-snackbar"],
    })
  }

  private showError(message: string): void {
    this.snackBar.open(message, "Close", {
      duration: 7000,
      panelClass: ["error-snackbar"],
    })
  }

  get userProfileControl() {
    return this.nutritionForm.get("userProfile")
  }
}
