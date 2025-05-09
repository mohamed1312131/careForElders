export interface User {
    id: string;
    firstName: string;
    lastName: string;
    email: string;
    phoneNumber: string;
    role: 'USER' | 'ADMINISTRATOR' | 'DOCTOR' | 'NURSE';
  }
  