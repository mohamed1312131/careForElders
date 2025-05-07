import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PhotoService {

  private apiUrl = 'http://localhost:8081/api/photos';


  constructor(private http: HttpClient) {}

  /**
   * Uploads a photo to the backend (which then sends it to Cloudinary)
   * @param file - The image file selected by the user
   * @returns Observable with Cloudinary secure URL
   */
  uploadPhoto(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<{ url: string }>(`${this.apiUrl}/upload`, formData);
  }

  /**
   * Upload with progress tracking (optional)
   */
  uploadPhotoWithProgress(file: File): Observable<HttpEvent<any>> {
    const formData = new FormData();
    formData.append('file', file);

    const req = new HttpRequest('POST', `${this.apiUrl}/upload`, formData, {
      reportProgress: true,
      responseType: 'json'
    });

    return this.http.request(req);
  }
}
