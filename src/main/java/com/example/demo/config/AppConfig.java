package comp.example.demo.config;

import net.datafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class AppConfig {

    @Bean
    public Faker faker() {
        return new Faker();
    }
}