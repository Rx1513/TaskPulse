package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfigurationPackage(basePackages = {"auth","web", "database", "tasks", "users"})
@SpringBootApplication(scanBasePackages = {"auth","web", "database", "tasks", "users"})
@EnableJpaRepositories(basePackages = "database.jpa")
public class HttpServer {
    public static void main(String[] args) {
        SpringApplication.run(HttpServer.class, args);
    }
}