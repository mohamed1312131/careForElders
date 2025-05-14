// src/app/billing/models/service-item.model.ts

export interface ServiceItem {
    id?: number;               // Optional ID for existing items
    serviceName: string;       // Name of the service (e.g., "Consultation")
    description: string;       // Detailed description
    rate: number;              // Price per unit
    quantity: number;          // Number of units
    amount?: number;           // Calculated total (rate * quantity)
  }
  
  // Optional: Utility functions for ServiceItem
 
  