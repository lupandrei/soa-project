package com.example.notification_service.event;

import com.example.notification_service.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingConfirmedPublisher {
    private final RabbitTemplate rabbitTemplate;

    public BookingConfirmedPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void bookingConfirmed(String username, String restaurantName) {
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                new BookingConfirmedEvent(username, restaurantName)
        );
    }
}
