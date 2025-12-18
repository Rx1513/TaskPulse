package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import tasks.Task;

@Controller
public class ContentController {

    private final List<Task> tasks = new ArrayList<>();
    private String currentUser = null;

    @GetMapping("/auth/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/auth/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @GetMapping(path = {"/tasks","/"})
    public ModelAndView tasks() {
        ModelAndView mv = new ModelAndView("tasks");
        mv.addObject("tasks", tasks);
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @GetMapping("/task/new")
    public ModelAndView newTaskForm() {
        ModelAndView mv = new ModelAndView("new_task");
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    @GetMapping("/task/show/{id}")
    public ModelAndView showTask(@PathVariable int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .map(task -> {
                    ModelAndView mv = new ModelAndView("show_task");
                    mv.addObject("task", task);
                    mv.addObject("currentUser", currentUser);
                    return mv;
                })
                .orElseGet(() -> new ModelAndView("redirect:/tasks"));
    }

    @GetMapping("/task/edit/{id}")
    public ModelAndView editTaskForm(@PathVariable int id) {
        return tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .map(task -> {
                    ModelAndView mv = new ModelAndView("edit_task");
                    mv.addObject("task", task);
                    mv.addObject("currentUser", currentUser);
                    return mv;
                })
                .orElseGet(() -> new ModelAndView("redirect:/tasks"));
    }
}