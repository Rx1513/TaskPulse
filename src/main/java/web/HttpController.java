package web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpController {
    @GetMapping("/auth/register")
    public String register() {
        return "Welcome to the registration page!";
    }

    @GetMapping("/auth/login")
    public String login() {
        return "Welcome to the login page!";
    }

    @GetMapping(path = {"/index", "/"})
    public String indexPreview() {
        return "Welcome to the main page!";
    }

    @GetMapping("/task/show/{id}")
    public String task() {
        return "Welcome to the task view!";
    }

    @GetMapping("/task/new")
    public String showTaskCreationPage() {
        return "Welcome to the task creation page!";
    }

    @PostMapping("/task/new")
    public String createTask() {
        return "Task created!";
    }
}

