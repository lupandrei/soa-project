package com.example.restaurant_service.repository;

import com.example.restaurant_service.data.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
