package rw.agriconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AgriconnectBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgriconnectBackendApplication.class, args);
    }
} 