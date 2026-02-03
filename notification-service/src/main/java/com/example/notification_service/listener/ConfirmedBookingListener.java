package com.example.notification_service.listener;

import com.example.notification_service.config.RabbitConfig;
import com.example.notification_service.event.BookingConfirmedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ConfirmedBookingListener {

    private final SimpMessagingTemplate messagingTemplate;

    public ConfirmedBookingListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private final Map<String, List<BookingConfirmedEvent>> analyticsStore = new ConcurrentHashMap<>();

    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void handle(BookingConfirmedEvent event) {
        System.out.println(
                "📨 Notification-service received booking confirmed: "
                        + event.username() + " " + event.restaurantName()
        );

        messagingTemplate.convertAndSend("/topic/bookings", event);
    }

    @KafkaListener(topics = "booking-analytics-topic", groupId = "analytics-group")
    public void consumeKafkaEvent(BookingConfirmedEvent event) {
        System.out.println("📊 [Kafka] Adaug în analytics: " + event.restaurantName() + " " + event.username());
        analyticsStore.computeIfAbsent(event.username(), k -> new CopyOnWriteArrayList<>())
                .add(event);
    }

    public List<BookingConfirmedEvent> getAnalyticsForUser(String username) {
        return analyticsStore.getOrDefault(username, Collections.emptyList());
    }
}
