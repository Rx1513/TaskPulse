package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"web", "database", "tasks", "users"})
public class HttpServer {
    public static void main(String[] args) {
        SpringApplication.run(HttpServer.class, args);
    }
}
