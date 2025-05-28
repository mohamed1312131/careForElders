import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment, CommentRequest, SentimentAnalysisResult, ComprehensiveAnalysis } from './models/comment.model';

@Injectable({
  providedIn: 'root'
})
export class CommentService {
  private apiUrl = 'http://localhost:8084/api/comments';

  constructor(private http: HttpClient) {}

  // Basic CRUD operations
  getComments(postId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/post/${postId}`);
  }

  // Alias method for post-list component compatibility
  getCommentsByPostId(postId: string): Observable<Comment[]> {
    return this.getComments(postId);
  }

  // Get paginated comments for a specific post
  getCommentsByPostIdPaginated(postId: string, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<any>(`${this.apiUrl}/post/${postId}/paginated`, { params });
  }

  // Get comment count for a specific post
  getCommentCount(postId: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/post/${postId}/count`);
  }

  getCommentById(commentId: string): Observable<Comment> {
    return this.http.get<Comment>(`${this.apiUrl}/${commentId}`);
  }

  addComment(comment: CommentRequest): Observable<Comment> {
    return this.http.post<Comment>(this.apiUrl, comment);
  }

  addReply(parentCommentId: string, reply: CommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/${parentCommentId}/reply`, reply);
  }

  updateComment(commentId: string, comment: CommentRequest): Observable<Comment> {
    return this.http.put<Comment>(`${this.apiUrl}/${commentId}`, comment);
  }

  deleteComment(commentId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${commentId}`);
  }

  // Like operations
  likeComment(commentId: string, userId: string): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/${commentId}/like`, { userId });
  }

  unlikeComment(commentId: string, userId: string): Observable<Comment> {
    return this.http.delete<Comment>(`${this.apiUrl}/${commentId}/like`, { 
      body: { userId } 
    });
  }

  // Get replies for a specific comment
  getReplies(commentId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/${commentId}/replies`);
  }

  // Reply to a comment (alternative method)
  replyToComment(commentId: string, commentRequest: CommentRequest): Observable<Comment> {
    return this.addReply(commentId, commentRequest);
  }

  // Get comments by user
  getCommentsByUser(userId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/user/${userId}`);
  }

  // Get top-level comments (no parent) for a post
  getTopLevelComments(postId: string): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/post/${postId}/top-level`);
  }

  // Get comment thread (comment with all its replies)
  getCommentThread(commentId: string): Observable<Comment> {
    return this.http.get<Comment>(`${this.apiUrl}/${commentId}/thread`);
  }

  // Report a comment
  reportComment(commentId: string, reason: string): Observable<void> {
    const body = { reason };
    return this.http.post<void>(`${this.apiUrl}/${commentId}/report`, body);
  }

  // Enhanced Sentiment Analysis operations
  analyzeText(text: string): Observable<SentimentAnalysisResult> {
    return this.http.post<SentimentAnalysisResult>(`${this.apiUrl}/analyze-sentiment`, { text });
  }

  performComprehensiveAnalysis(text: string): Observable<ComprehensiveAnalysis> {
    return this.http.post<ComprehensiveAnalysis>(`${this.apiUrl}/comprehensive-analysis`, { text });
  }

  reanalyzeComment(commentId: string): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/${commentId}/reanalyze`, {});
  }

  // Batch sentiment analysis for multiple comments
  batchAnalyzeComments(commentIds: string[]): Observable<Comment[]> {
    return this.http.post<Comment[]>(`${this.apiUrl}/batch-analyze`, { commentIds });
  }

  // Search and filter operations
  searchComments(query: string): Observable<Comment[]> {
    const params = new HttpParams().set('query', query);
    return this.http.get<Comment[]>(`${this.apiUrl}/search`, { params });
  }

  getCommentsBySentiment(sentiment: string): Observable<Comment[]> {
    const params = new HttpParams().set('sentiment', sentiment);
    return this.http.get<Comment[]>(`${this.apiUrl}/by-sentiment`, { params });
  }

  getCommentsByEmotion(emotion: string): Observable<Comment[]> {
    const params = new HttpParams().set('emotion', emotion);
    return this.http.get<Comment[]>(`${this.apiUrl}/by-emotion`, { params });
  }

  getCommentsByKeyword(keyword: string): Observable<Comment[]> {
    const params = new HttpParams().set('keyword', keyword);
    return this.http.get<Comment[]>(`${this.apiUrl}/by-keyword`, { params });
  }

  // Advanced filtering
  getCommentsByPostAndSentiment(postId: string, sentiment: string): Observable<Comment[]> {
    const params = new HttpParams()
      .set('sentiment', sentiment);
    return this.http.get<Comment[]>(`${this.apiUrl}/post/${postId}/by-sentiment`, { params });
  }

  getCommentsByPostAndEmotion(postId: string, emotion: string): Observable<Comment[]> {
    const params = new HttpParams()
      .set('emotion', emotion);
    return this.http.get<Comment[]>(`${this.apiUrl}/post/${postId}/by-emotion`, { params });
  }

  // Statistics
  getSentimentStatistics(postId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/post/${postId}/sentiment-stats`);
  }

  getEmotionStatistics(postId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/post/${postId}/emotion-stats`);
  }

  // Overall statistics across all posts
  getOverallSentimentStatistics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/overall-sentiment-stats`);
  }

  getOverallEmotionStatistics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/overall-emotion-stats`);
  }

  // Trending and popular comments
  getTrendingComments(limit: number = 10): Observable<Comment[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Comment[]>(`${this.apiUrl}/trending`, { params });
  }

  getMostLikedComments(postId: string, limit: number = 5): Observable<Comment[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<Comment[]>(`${this.apiUrl}/post/${postId}/most-liked`, { params });
  }

  // Moderation helpers
  getFlaggedComments(): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/flagged`);
  }

  getCommentsByModerationStatus(status: string): Observable<Comment[]> {
    const params = new HttpParams().set('status', status);
    return this.http.get<Comment[]>(`${this.apiUrl}/moderation-status`, { params });
  }

  // Bulk operations
  bulkDeleteComments(commentIds: string[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/bulk-delete`, { commentIds });
  }

  bulkUpdateSentiment(commentIds: string[]): Observable<Comment[]> {
    return this.http.post<Comment[]>(`${this.apiUrl}/bulk-update-sentiment`, { commentIds });
  }

  // Export functionality
  exportCommentsToCSV(postId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/post/${postId}/export/csv`, { 
      responseType: 'blob' 
    });
  }

  exportSentimentAnalysisToCSV(postId: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/post/${postId}/export/sentiment-csv`, { 
      responseType: 'blob' 
    });
  }

  // Real-time updates (if using WebSocket)
  subscribeToCommentUpdates(postId: string): Observable<Comment> {
    // This would typically use WebSocket or Server-Sent Events
    // For now, returning a placeholder
    return new Observable(observer => {
      // WebSocket implementation would go here
    });
  }

  // Analytics helpers
  getCommentEngagementMetrics(postId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/post/${postId}/engagement-metrics`);
  }

  getCommentActivityTimeline(postId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/post/${postId}/activity-timeline`);
  }

  // User interaction tracking
  markCommentAsRead(commentId: string, userId: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${commentId}/mark-read`, { userId });
  }

  getUnreadCommentsCount(userId: string): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count/${userId}`);
  }
}