package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    @Autowired
    Environment environment;
    @Value("${spring.profiles.active:Default}")
    String activeProfile;

    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }

    @Bean
    public void printProfile() {
        System.out.println("Repo used for config files: " + environment.getProperty("spring.cloud.config.server.git.uri"));
        System.out.println("Active spring profile: " + activeProfile);
    }
}