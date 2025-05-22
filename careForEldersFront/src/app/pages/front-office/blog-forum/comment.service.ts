import { Injectable } from "@angular/core"
import { HttpClient } from "@angular/common/http"
import { Observable } from "rxjs"
import { Comment } from "./models/comment.model"

@Injectable({
  providedIn: "root",
})
export class CommentService {
  private apiUrl = "http://localhost:8084/api/" // Updated to match your API URL

  constructor(private http: HttpClient) {}

  getCommentsByPostId(postId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/posts/${postId}/comments`)
  }

  createComment(postId: string, commentData: any): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/posts/${postId}/comments`, commentData)
  }

  updateComment(postId: string, commentId: string, commentData: any): Observable<Comment> {
    return this.http.put<Comment>(`${this.apiUrl}/posts/${postId}/comments/${commentId}`, commentData)
  }

  deleteComment(postId: string, commentId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/posts/${postId}/comments/${commentId}`)
  }

  likeComment(postId: string, commentId: string): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/posts/${postId}/comments/${commentId}/like`, {})
  }

  unlikeComment(postId: string, commentId: string): Observable<Comment> {
    return this.http.delete<Comment>(`${this.apiUrl}/posts/${postId}/comments/${commentId}/like`)
  }
}