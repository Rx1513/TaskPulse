package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ContentController {

    private List<Task> tasks = new ArrayList<>();

    @GetMapping("/auth/register")
    public ModelAndView register() {
        ModelAndView mv = new ModelAndView("register");
        mv.getModel().put("data", "Welcome to registration page!");
        return mv;
    }

    @GetMapping("/auth/login")
    public ModelAndView login() {
        ModelAndView mv = new ModelAndView("login");
        mv.getModel().put("data", "Welcome to login page!");
        return mv;
    }

    @GetMapping("/tasks")
    public ModelAndView tasks() {
        ModelAndView mv = new ModelAndView("tasks");
        mv.getModel().put("tasks", tasks);
        return mv;
    }

    @GetMapping("/task/new")
    public ModelAndView newTask() {
        return new ModelAndView("new_task");
    }

    @PostMapping("/task/new")
    public RedirectView createTask(
            @RequestParam String title,
            @RequestParam String project,
            @RequestParam String status,
            @RequestParam String dueDate
    ) {
        System.out.println("POST /task/new: " + title + ", " + project + ", " + status + ", " + dueDate);
        tasks.add(new Task(title, project, status, dueDate));
        return new RedirectView("/tasks");
    }

    @GetMapping("/task/show/{id}")
    public ModelAndView showTask(@PathVariable int id) {
        if (id < 1 || id > tasks.size()) {
            return new ModelAndView("redirect:/tasks"); // если нет такой задачи
        }

        Task task = tasks.get(id - 1); // так как id мы делаем от 1
        ModelAndView mv = new ModelAndView("show_task"); // шаблон show_task.html
        mv.getModel().put("task", task);
        return mv;
    }

    // Изменен метод удаления задачи
    @PostMapping("/task/delete/{id}")
    public RedirectView deleteTask(@PathVariable int id) {
        tasks.removeIf(task -> task.getId() == id); // удаляем задачу по id
        return new RedirectView("/tasks"); // редирект обратно на список задач
    }

    public static class Task {
        private static int counter = 1; // счётчик ID
        public int id;
        public String title;
        public String project;
        public String status;
        public String dueDate;

        public Task(String title, String project, String status, String dueDate) {
            this.id = counter++;
            this.title = title;
            this.project = project;
            this.status = status;
            this.dueDate = dueDate;
        }

        // геттеры (Thymeleaf работает с public или с геттерами)
        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getProject() { return project; }
        public String getStatus() { return status; }
        public String getDueDate() { return dueDate; }
    }
}
