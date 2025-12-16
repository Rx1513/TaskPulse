package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ContentController {

    private final List<Task> tasks = new ArrayList<>();

    @GetMapping("/auth/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @GetMapping("/auth/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @GetMapping("/tasks")
    public ModelAndView tasks() {
        ModelAndView mv = new ModelAndView("tasks");
        mv.addObject("tasks", tasks);
        return mv;
    }

    @GetMapping("/task/new")
    public ModelAndView newTask() {
        return new ModelAndView("new_task");
    }

    @PostMapping("/task/new")
    public RedirectView createTask(
            @RequestParam String project,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String assignee,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        tasks.add(new Task(
                project,
                title,
                description,
                assignee,
                startDate,
                endDate
        ));
        return new RedirectView("/tasks");
    }

    @GetMapping("/task/show/{id}")
    public ModelAndView showTask(@PathVariable int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .map(task -> {
                    ModelAndView mv = new ModelAndView("show_task");
                    mv.addObject("task", task);
                    return mv;
                })
                .orElseGet(() -> new ModelAndView("redirect:/tasks"));
    }

    @PostMapping("/task/delete/{id}")
    public RedirectView deleteTask(@PathVariable int id) {
        tasks.removeIf(task -> task.getId() == id);
        return new RedirectView("/tasks");
    }

    public static class Task {

        private static int counter = 1;

        private final int id;
        private final String project;
        private final String title;
        private final String description;
        private final String assignee;
        private final String startDate;
        private final String endDate;

        public Task(String project,
                    String title,
                    String description,
                    String assignee,
                    String startDate,
                    String endDate) {
            this.id = counter++;
            this.project = project;
            this.title = title;
            this.description = description;
            this.assignee = assignee;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public int getId() { return id; }
        public String getProject() { return project; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getAssignee() { return assignee; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
    }
}
