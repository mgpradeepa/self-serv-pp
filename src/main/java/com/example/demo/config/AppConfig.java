package com.example.demo.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;

public class AppConfig {

    @Bean
    public Faker faker() {
        return new Faker();
    }
}