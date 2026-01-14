package com.example.booking_service.service;

import com.example.booking_service.data.Booking;
import com.example.booking_service.dto.BookingDto;
import com.example.booking_service.event.BookingConfirmedPublisher;
//import com.example.booking_service.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

//    @Autowired
//    private BookingRepository repository;
    @Autowired
    BookingConfirmedPublisher bookingConfirmedPublisher;

    public Booking createBooking(BookingDto booking) {
        bookingConfirmedPublisher.bookingConfirmed(booking.userEmail(), booking.restaurantName());
        return new Booking();
    }
}
