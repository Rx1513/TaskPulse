package web;

import database.TaskRepository;
import database.UserRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import tasks.Task;
import users.User;

@Controller
public class ContentController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    @GetMapping("/auth/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/auth/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @GetMapping(path = { "/tasks", "/" })
    public ModelAndView tasks(Principal principal) {
        ModelAndView mv = new ModelAndView("tasks");
        Optional<User> curerentUser = userRepository.findUserByName(principal.getName());
        if (curerentUser.isEmpty()) {
            throw new EmptyResultDataAccessException("Текущий авторизованный пользователь не найден! " + principal.getName(), 1);
        }
        mv.addObject("tasks", taskRepository.getTasksPreviewsByUser(curerentUser.get()));
        mv.addObject("currentUser", principal.getName());
        return mv;
    }

    @GetMapping("/task/new")
    public ModelAndView newTaskForm(Principal principal) {
        ModelAndView mv = new ModelAndView("new_task");
        mv.addObject("currentUser", principal.getName());
        return mv;
    }

    @GetMapping("/task/show/{id}")
    public ModelAndView showTask(@PathVariable int id, Principal principal) {
        ModelAndView mv = new ModelAndView("show_task");
        mv.addObject("currentUser", principal.getName());
        Optional<Task> task = taskRepository.findTaskById(id);
        if (task.isEmpty()) {
            return new ModelAndView("redirect:/tasks");
        }
        mv.addObject("task", task.get());
        return mv;
    }

    @GetMapping("/task/edit/{id}")
    public ModelAndView editTaskForm(@PathVariable int id, Principal principal) {
        ModelAndView mv = new ModelAndView("edit_task");
        mv.addObject("currentUser", principal.getName());
        Optional<Task> task = taskRepository.findTaskById(id);
        if (task.isEmpty()) {
            return new ModelAndView("redirect:/tasks");
        }
        mv.addObject("task", task.get());
        return mv;
    }
}