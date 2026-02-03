import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Restaurant } from './restaurant.model';

@Injectable({ providedIn: 'root' })
export class RestaurantService {
  private baseUrl = 'http://localhost:8082';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<Restaurant[]>(`${this.baseUrl}/restaurants`);
  }
}
