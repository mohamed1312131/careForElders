import { Component } from "@angular/core"

@Component({
  selector: "app-loading-spinner",
  template: `
    <div class="spinner-container">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
    </div>
  `,
  styles: [
    `
    .spinner-container {
      display: flex;
      justify-content: center;
      align-items: center;
      padding: 20px;
    }
  `,
  ],
})
export class LoadingSpinnerComponent {}
