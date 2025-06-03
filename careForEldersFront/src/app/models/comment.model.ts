export interface Comment {
    commentId?: string; // Assuming backend generates this ID
    userId: string;
    comment: string;
    timestamp?: Date; // Assuming conversion from backend timestamp
  }
  