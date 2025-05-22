import { Like } from './like.model';

export interface Post {
  id: string;
  title: string;
  content: string;
  authorId: string;
  authorName: string;
  tags: string[];
  likes: Like[];
  viewCount: number;
  commentsCount: number;
  createdAt: string;
  updatedAt: string;
  published: boolean;
}

export interface PostRequest {
  title: string;
  content: string;
  tags: string[];
  published: boolean;
}