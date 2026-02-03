import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { RestaurantService } from './restaurant.service';
import { Restaurant } from './restaurant.model';
import { AuthService } from '../services/auth.service';
import { BookingDto, BookingService } from '../booking.service';
import { FaasService } from '../faas.service';
import { WebsocketService } from '../websocket.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-restaurants',
  templateUrl: './restaurants.component.html',
  styleUrls: ['./restaurants.component.css']
})
export class RestaurantsComponent implements OnInit, OnDestroy {
  restaurants: Restaurant[] = [];
  error: string = '';
  
  estimates: { [key: string]: any } = {};
  groupSizes: { [key: string]: number } = {};
  loadingEstimates: { [key: string]: boolean } = {};

  private wsSubscription?: Subscription;

  constructor(
    private restaurantService: RestaurantService,
    private auth: AuthService,
    private bookingService: BookingService,
    private faasService: FaasService,
    private wsService: WebsocketService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadRestaurants();
    this.setupNotifications();
  }

  ngOnDestroy(): void {
    // Închidem abonamentul când plecăm de pe pagină
    if (this.wsSubscription) {
      this.wsSubscription.unsubscribe();
    }
  }

setupNotifications(): void {
  this.wsSubscription = this.wsService.bookingNotifications.subscribe({
    next: (event: any) => {
      const currentUser = this.auth.getUsername();
      
      if (event.username === currentUser || event.userEmail === currentUser) {
        console.log('🔔 Eveniment primit din RabbitMQ:', event);

        // POPUP FRUMOS ÎN LOC DE ALERT
        Swal.fire({
          title: 'Confirmed Reseravation!',
          text: `Your Reservation at "${event.restaurantName}" was confirmed!.`,
          icon: 'success',
          confirmButtonText: 'Super!',
          confirmButtonColor: '#28a745',
          timer: 5000, // Se închide singur după 5 secunde dacă userul nu apasă nimic
          timerProgressBar: true,
          toast: false, // Dacă vrei să fie un popup central mare
          position: 'center'
        });
      }
    },
    error: (err: any) => console.error('Eroare la primirea notificării:', err)
  });
}

  loadRestaurants(): void {
    this.restaurantService.getAll().subscribe({
      next: (data) => {
        this.restaurants = data;
        this.restaurants.forEach(r => {
          this.groupSizes[r.name] = 2;
          this.loadingEstimates[r.name] = false;
        });
      },
      error: () => (this.error = 'Nu pot contacta API Gateway.')
    });
  }

  calculateEstimate(r: Restaurant): void {
    const size = this.groupSizes[r.name];
    const cuisine = r.cuisineType || 'general';
    this.loadingEstimates[r.name] = true;

    this.faasService.getEstimate(cuisine, size).subscribe({
      next: (res) => {
        this.estimates[r.name] = res;
        this.loadingEstimates[r.name] = false;
      },
      error: () => {
        this.loadingEstimates[r.name] = false;
        alert('Eroare la apelul funcției FaaS Python.');
      }
    });
  }

  book(r: Restaurant): void {
    const userEmail = this.auth.getUsername(); 

    if (!userEmail) {
      return;
    }

    const bookingData: BookingDto = {
      userEmail: userEmail,
      restaurantName: r.name
    };

    // Trimitem cererea HTTP
    this.bookingService.createBooking(bookingData).subscribe({
      next: () => {
        console.log('Cerere trimisă către backend. Așteptăm confirmarea prin RabbitMQ...');
      },
      error: () => alert('Eroare la trimiterea rezervării.')
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  viewMyHistory() {
  const username = this.auth.getUsername();
  if (!username) return;

  this.faasService.getUserHistory(username).subscribe({
    next: (history) => {
      if (history.length === 0) {
        Swal.fire('Istoric gol', 'Nu am găsit nicio rezervare.', 'info');
        return;
      }

      // Construim un HTML pentru listă
      const historyHtml = history.map(item => `
        <div style="text-align: left; border-bottom: 1px solid #eee; padding: 10px;">
          <strong>Restaurant:</strong> ${item.restaurantName} <br>
          <small class="text-muted">Status: Confirmat</small>
        </div>
      `).join('');

      Swal.fire({
        title: `Istoricul tău, ${username}`,
        html: `<div style="max-height: 400px; overflow-y: auto;">${historyHtml}</div>`,
        icon: 'info',
        confirmButtonText: 'Închide',
        confirmButtonColor: '#0d6efd'
      });
    },
    error: () => Swal.fire('Eroare', 'Nu am putut prelua istoricul din Kafka.', 'error')
  });
}
}