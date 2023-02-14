package com.example;

import com.example.business.events.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "orderNotification")
    public void handleNotification(OrderEvent orderEvent) {
        log.info("Order event received: " + orderEvent);
        // Add logic to send email/text notification
        log.info("Business logic not yet implemented. No further action taken.");
    }
}