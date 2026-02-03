package com.example.notification_service.controller;

import com.example.notification_service.event.BookingConfirmedEvent;
import com.example.notification_service.listener.ConfirmedBookingListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final ConfirmedBookingListener confirmedBookingListener;

    public AnalyticsController(ConfirmedBookingListener confirmedBookingListener) {
        this.confirmedBookingListener = confirmedBookingListener;
    }

    @GetMapping("/{username}")
    public List<BookingConfirmedEvent> getHistory(@PathVariable String username) {
        return confirmedBookingListener.getAnalyticsForUser(username);
    }
}
