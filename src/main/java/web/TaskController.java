package web;

import database.TaskRepository;
import database.UserRepository;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import tasks.Status;
import tasks.Task;
import tasks.TaskService;
import users.User;

@Controller
@RequiredArgsConstructor
public class TaskController {
    @Autowired
    UserRepository userRepository;

    final TaskService taskService;

    @PostMapping("/task/new")
    public RedirectView createTask(
            @RequestParam String project,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String assignee,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDate endDate,
            Principal principal
    ) {
        Optional<User> creator = userRepository.findUserByName(principal.getName());
        if (creator.isEmpty()) {
            throw new EmptyResultDataAccessException("Создатель задачи не найден! " + principal.getName(), 1);
        }

        Optional<User> assigneeUser = userRepository.findUserByName(assignee);
        if (assigneeUser.isEmpty()) {
            throw new EmptyResultDataAccessException("Исполнитель задачи не найден! " + assignee, 1);
        }

        taskService.createTask(
                project,
                title,
                description,
                creator.get(),
                assigneeUser.get(),
                startDate,
                endDate,
                Status.NEW);

        return new RedirectView("/tasks");
    }

    @PostMapping("/task/delete/{id}")
    public RedirectView deleteTask(@PathVariable int id) {
        taskService.deleteTaskById(id);
        return new RedirectView("/tasks");
    }
}
