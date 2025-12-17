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
    private String currentUser = null; // хранение текущего пользователя

    @GetMapping("/auth/register")
    public ModelAndView register() {
        return new ModelAndView("register");
    }

    @PostMapping("/auth/register")
    public RedirectView doRegister(@RequestParam String username) {
        currentUser = username; // "регистрируем" пользователя
        return new RedirectView("/tasks");
    }

    @GetMapping("/auth/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @PostMapping("/auth/login")
    public RedirectView doLogin(@RequestParam String username) {
        currentUser = username; // логиним пользователя
        return new RedirectView("/tasks");
    }

    @GetMapping("/auth/logout")
    public RedirectView logout() {
        currentUser = null;
        return new RedirectView("/auth/login");
    }

    @GetMapping("/tasks")
    public ModelAndView tasks() {
        ModelAndView mv = new ModelAndView("tasks");
        mv.addObject("tasks", tasks);
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    // Аналогично передаем currentUser на страницы task/show, task/new, task/edit
    @GetMapping("/task/show/{id}")
    public ModelAndView showTask(@PathVariable int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .map(task -> {
                    ModelAndView mv = new ModelAndView("show_task");
                    mv.addObject("task", task);
                    mv.addObject("currentUser", currentUser);
                    return mv;
                })
                .orElseGet(() -> new ModelAndView("redirect:/tasks"));
    }

    // Показать форму создания задачи
    @GetMapping("/task/new")
    public ModelAndView newTaskForm() {
        ModelAndView mv = new ModelAndView("new_task");
        mv.addObject("currentUser", currentUser);
        return mv;
    }

    // Обработка формы создания задачи
    @PostMapping("/task/new")
    public RedirectView createTask(
            @RequestParam String project,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String assignee,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "Новая") String status
    ) {
        Task task = new Task(project, title, description, assignee, startDate, endDate);
        task.setStatus(status);
        tasks.add(task);
        return new RedirectView("/tasks");
    }

    @GetMapping("/task/edit/{id}")
    public ModelAndView editTaskForm(@PathVariable int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .map(task -> {
                    ModelAndView mv = new ModelAndView("edit_task");
                    mv.addObject("task", task);
                    mv.addObject("currentUser", currentUser);
                    return mv;
                })
                .orElseGet(() -> new ModelAndView("redirect:/tasks"));
    }

    // Обработать форму редактирования
    @PostMapping("/task/edit/{id}")
    public RedirectView editTask(
            @PathVariable int id,
            @RequestParam String project,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam String assignee,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String status
    ) {
        tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .ifPresent(task -> {
                    task.setProject(project);
                    task.setTitle(title);
                    task.setDescription(description);
                    task.setAssignee(assignee);
                    task.setStartDate(startDate);
                    task.setEndDate(endDate);
                    task.setStatus(status);
                });
        return new RedirectView("/task/show/" + id);
    }

    @PostMapping("/task/delete/{id}")
    public RedirectView deleteTask(@PathVariable int id) {
        tasks.removeIf(task -> task.getId() == id);
        return new RedirectView("/tasks");
    }




    public static class Task {

        private static int counter = 1;

        private final int id;
        private String project;
        private String title;
        private String description;
        private String assignee;
        private String startDate;
        private String endDate;
        private String status;

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
            this.status = "Новая";
        }

        public int getId() { return id; }
        public String getProject() { return project; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getAssignee() { return assignee; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getStatus() { return status; }

        // Добавляем сеттеры для редактирования
        public void setProject(String project) { this.project = project; }
        public void setTitle(String title) { this.title = title; }
        public void setDescription(String description) { this.description = description; }
        public void setAssignee(String assignee) { this.assignee = assignee; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public void setStatus(String status) { this.status = status; }
    }
}
