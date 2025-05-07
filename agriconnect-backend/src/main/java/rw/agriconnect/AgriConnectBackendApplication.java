package rw.agriconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "rw.agriconnect",
    "rw.agriconnect.backend",
    "rw.agriconnect.config",
    "rw.agriconnect.security"
})
public class AgriConnectBackendApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(AgriConnectBackendApplication.class);
        application.setAdditionalProfiles("dev");
        application.run(args);
    }
} 