package com.example.notification_service.listener;

import com.example.notification_service.config.RabbitConfig;
import com.example.notification_service.event.BookingConfirmedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ConfirmedBookingListener {

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(BookingConfirmedEvent event) {
        System.out.println(
                "📨 Notification-service received booking confirmed: "
                        + event.username() +  " " + event.restaurantName()
        );
    }
}
