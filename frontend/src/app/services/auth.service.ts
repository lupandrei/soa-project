import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private baseUrl = 'http://localhost:8082'; // gateway

  constructor(private http: HttpClient) {}

login(payload: { email: string; password: string }) {
  return this.http.post(`${this.baseUrl}/auth/login`, payload, { responseType: 'text' }).pipe(
    tap(token => {
      localStorage.setItem('token', token);
      localStorage.setItem('email', payload.email);
    })
  );
}


  register(payload: { email: string; password: string }) {
    return this.http.post(`${this.baseUrl}/auth/register`, payload);
  }

  logout() {
    localStorage.removeItem('token');
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUsername(): string | null {
    return localStorage.getItem('email')
  }
}
