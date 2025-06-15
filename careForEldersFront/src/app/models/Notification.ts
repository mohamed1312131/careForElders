export interface Notification {
  id: string;
  userId: string;
  type: string;
  message: string;
  sentTime: Date | string;
  responseDeadline: Date | string;
  responded: boolean;
  active: boolean;
  response?: string;
}