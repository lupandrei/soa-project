package com.example.booking_service.event;

import com.example.booking_service.config.RabbitConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BookingConfirmedPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BookingConfirmedPublisher(RabbitTemplate rabbitTemplate, KafkaTemplate<String,Object> kafkaTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void bookingConfirmed(String username, String restaurantName) {
        BookingConfirmedEvent event = new BookingConfirmedEvent(username, restaurantName);

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                event
        );
        kafkaTemplate.send("booking-analytics-topic", username, event);
    }
}
