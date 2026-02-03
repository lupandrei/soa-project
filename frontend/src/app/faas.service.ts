import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FaasService {
  private url = 'http://localhost:8082/faas/estimate/';

  constructor(private http: HttpClient) {}

  getEstimate(cuisine: string, groupSize: number): Observable<any> {
    const isWeekend = [0, 6].includes(new Date().getDay());
    
    const payload = {
      cuisine: cuisine,
      group_size: groupSize,
      is_weekend: isWeekend
    };

    return this.http.post<any>(this.url, payload);
  }

  getUserHistory(username: string): Observable<any[]> {
    return this.http.get<any[]>(`http://localhost:8082/analytics/${username}`);
}
}