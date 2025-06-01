import { Like } from './like.model';

export interface PostImage {
  id: string;
  url: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  altText?: string;
  caption?: string;
  order: number;
  uploadedAt: Date;
}

export interface Post {
  id: string;
  title: string;
  content: string;
  authorId: string;
  authorName: string;
  createdAt: Date;
  updatedAt?: Date;
  likes: Like[];
  tags?: string[];
  commentsCount?: number;
  viewCount?: number;
  isDeleted?: boolean;
  
  // Image fields
  featuredImageUrl?: string;
  featuredImageName?: string;
  featuredImageType?: string;
  featuredImageSize?: number;
  imageAltText?: string;
  imageCaption?: string;
  additionalImages?: PostImage[];
}

export interface PostRequest {
  title: string;
  content: string;
  tags?: string[];
  
  // Image fields
  featuredImageUrl?: string;
  featuredImageName?: string;
  featuredImageType?: string;
  featuredImageSize?: number;
  imageAltText?: string;
  imageCaption?: string;
  additionalImages?: {
    url: string;
    fileName: string;
    fileType: string;
    fileSize: number;
    altText?: string;
    caption?: string;
    order: number;
  }[];
}

export interface PostResponse {
  content: Post[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}