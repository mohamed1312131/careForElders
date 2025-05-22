import { Like } from './like.model';

export interface Comment {
  id: string;
  postId: string;
  content: string;
  authorId: string;
  authorName: string;
  likes: Like[];
  createdAt: string;
  updatedAt: string;
  isDeleted: boolean;
  parentCommentId?: string;
  replies?: Comment[];
}