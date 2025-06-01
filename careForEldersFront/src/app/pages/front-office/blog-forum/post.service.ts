import { Injectable } from "@angular/core"
//import { HttpClient, HttpParams } from "@angular/common/http"
import { Observable } from "rxjs"
import { Post, PostRequest, PostResponse } from "./models/post.model"
import { HttpClient, HttpParams } from "@angular/common/http";

@Injectable({
  providedIn: "root",
})
export class PostService {
  private apiUrl = "http://localhost:8084/api/posts" // Updated to match your API URL
  constructor(private http: HttpClient) {}
// private apiUrl = "http://localhost:8084/api/speech-to-text"  
  getPosts(page: number = 0, size: number = 10): Observable<PostResponse> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PostResponse>(this.apiUrl, { params });
  }

  getPostById(id: string): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  createPost(postRequest: PostRequest): Observable<Post> {
    return this.http.post<Post>(this.apiUrl, postRequest);
  }

  updatePost(id: string, postRequest: PostRequest): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/${id}`, postRequest);
  }

  deletePost(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  likePost(id: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/like`, {});
  }

  unlikePost(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/like`);
  }

  searchPostsByTitle(title: string): Observable<Post[]> {
    const params = new HttpParams().set('title', title);
    return this.http.get<Post[]>(`${this.apiUrl}/search`, { params });
  }

  searchPostsByContent(content: string): Observable<Post[]> {
    const params = new HttpParams().set('content', content);
    return this.http.get<Post[]>(`${this.apiUrl}/search`, { params });
  }

  searchPostsByTag(tag: string): Observable<Post[]> {
    const params = new HttpParams().set('tag', tag);
    return this.http.get<Post[]>(`${this.apiUrl}/search`, { params });
  }

  getPostsByUser(userId: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/user/${userId}`);
  }
  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post(`${this.apiUrl}/upload-image`, formData);
  }
  
  uploadMultipleImages(files: File[]): Observable<any> {
    const formData = new FormData();
    files.forEach(file => {
      formData.append('files', file);
    });
    
    return this.http.post(`${this.apiUrl}/upload-images`, formData);
  }
  
  createPostWithImages(postData: PostRequest, featuredImage?: File, additionalImages?: File[]): Observable<Post> {
    const formData = new FormData();
    formData.append('post', new Blob([JSON.stringify(postData)], { type: 'application/json' }));
    
    if (featuredImage) {
      formData.append('featuredImage', featuredImage);
    }
    
    if (additionalImages && additionalImages.length > 0) {
      additionalImages.forEach(file => {
        formData.append('additionalImages', file);
      });
    }
    
    return this.http.post<Post>(`${this.apiUrl}/with-images`, formData);
  }


}