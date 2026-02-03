import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface BookingDto {
  userEmail: string;
  restaurantName: string;
}

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private apiUrl = 'http://localhost:8082/bookings'; 
  constructor(private http: HttpClient) {}

  createBooking(booking: BookingDto): Observable<any> {
    return this.http.post<any>(this.apiUrl, booking);
  }
}