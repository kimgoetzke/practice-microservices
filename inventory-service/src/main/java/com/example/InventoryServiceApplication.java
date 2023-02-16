package com.example;

import com.example.business.Inventory;
import com.example.persistence.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner createTestInitialInventory(InventoryRepository inventoryRepository) {
        return args -> {
            Inventory scooter = new Inventory();
            scooter.setName("Scooter");
            scooter.setQuantity(100);

            Inventory bicycle = new Inventory();
            bicycle.setName("Bus");
            bicycle.setQuantity(0);

            Inventory car1 = new Inventory();
            car1.setName("Car");
            car1.setQuantity(3);

            Inventory car2 = new Inventory();
            car2.setName("Bicycle");
            car2.setQuantity(20);

            inventoryRepository.save(scooter);
            inventoryRepository.save(bicycle);
            inventoryRepository.save(car1);
            inventoryRepository.save(car2);
        };
    }
}