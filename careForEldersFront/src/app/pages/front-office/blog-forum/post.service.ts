import { Injectable } from "@angular/core"
import { HttpClient, HttpParams } from "@angular/common/http"
import type { Observable } from "rxjs"

@Injectable({
  providedIn: "root",
})
export class PostService {
  private apiUrl = "http://localhost:8085/api/posts"

  constructor(private http: HttpClient) {}

  getAllPosts(page = 0, size = 10, sortBy = "createdAt", direction = "desc"): Observable<any> {
    const params = new HttpParams()
      .set("page", page.toString())
      .set("size", size.toString())
      .set("sortBy", sortBy)
      .set("direction", direction)

    return this.http.get<any>(this.apiUrl, { params })
  }

  getPostById(id: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`)
  }

  getPostsByAuthor(authorId: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/author/${authorId}`)
  }

  searchPosts(title?: string, content?: string, tag?: string): Observable<any[]> {
    let params = new HttpParams()

    if (title) {
      params = params.set("title", title)
    }

    if (content) {
      params = params.set("content", content)
    }

    if (tag) {
      params = params.set("tag", tag)
    }

    return this.http.get<any[]>(`${this.apiUrl}/search`, { params })
  }

  createPost(postRequest: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, postRequest)
  }

  updatePost(id: string, postRequest: any): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}`, postRequest)
  }

  deletePost(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }
}
