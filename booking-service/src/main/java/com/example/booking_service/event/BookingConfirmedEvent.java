package com.example.booking_service.event;

public record BookingConfirmedEvent(String username, String restaurantName) {
}
