import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import { Observable } from "rxjs"
import { Post } from "./models/post.model"

@Injectable({
  providedIn: "root",
})
export class PostService {
  private apiUrl = "http://localhost:8084/api" // Updated to match your API URL

  constructor(private http: HttpClient) {}

  getPosts(page: number = 0, size: number = 10): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/posts?page=${page}&size=${size}`)
  }

  getPostById(id: string): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/posts/${id}`)
  }

  createPost(postData: any): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/posts`, postData)
  }

  updatePost(id: string, postData: any): Observable<Post> {
    return this.http.put<Post>(`${this.apiUrl}/posts/${id}`, postData)
  }

  deletePost(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/posts/${id}`)
  }

  likePost(id: string): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/posts/${id}/like`, {})
  }

  unlikePost(id: string): Observable<Post> {
    return this.http.delete<Post>(`${this.apiUrl}/posts/${id}/like`)
  }

  searchPostsByTitle(title: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts/search?title=${encodeURIComponent(title)}`)
  }

  searchPostsByContent(content: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts/search?content=${encodeURIComponent(content)}`)
  }

  searchPostsByTag(tag: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/posts/search?tag=${encodeURIComponent(tag)}`)
  }
}