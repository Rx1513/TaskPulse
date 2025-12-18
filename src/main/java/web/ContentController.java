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
    private String currentUser = null;

    /* ---------- AUTH ---------- */

    @GetMapping("/auth/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @PostMapping("/auth/login")
    public RedirectView doLogin(@RequestParam String username) {
        currentUser = username;
        return new RedirectView("/tasks");
    }

    @GetMapping("/auth/logout")
    public RedirectView logout() {
        currentUser = null;
        return new RedirectView("/auth/login");
    }

    /* ---------- TASKS ---------- */

    @GetMapping("/tasks")
    public ModelAndView tasks() {
        ModelAndView mv = new ModelAndView("tasks");
        mv.addObject("tasks", tasks);
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

    @GetMapping("/task/new")
    public ModelAndView newTaskForm() {
        ModelAndView mv = new ModelAndView("new_task");
        mv.addObject("currentUser", currentUser);
        return mv;
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
        tasks.add(new Task(project, title, description, assignee, startDate, endDate, currentUser));
        return new RedirectView("/tasks");
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

    @PostMapping("/task/edit/{id}")
    public RedirectView editTask(@PathVariable int id,
                                 @RequestParam String project,
                                 @RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam String assignee,
                                 @RequestParam String startDate,
                                 @RequestParam String endDate,
                                 @RequestParam String status) {

        tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .ifPresent(t -> {
                    t.project = project;
                    t.title = title;
                    t.description = description;
                    t.assignee = assignee;
                    t.startDate = startDate;
                    t.endDate = endDate;
                    t.status = status;
                });

        return new RedirectView("/task/show/" + id);
    }

    @PostMapping("/task/delete/{id}")
    public RedirectView deleteTask(@PathVariable int id) {
        tasks.removeIf(t -> t.getId() == id);
        return new RedirectView("/tasks");
    }

    /* ---------- COMMENTS ---------- */

    @PostMapping("/task/{id}/comment")
    public RedirectView addComment(@PathVariable int id,
                                   @RequestParam String text) {

        tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .ifPresent(t -> t.comments.add(new Comment(currentUser, text)));

        return new RedirectView("/task/show/" + id);
    }

    /* ---------- watchers ---------- */

    @PostMapping("/task/{id}/observe")
    public RedirectView subscribe(@PathVariable int id) {
        tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .ifPresent(t -> t.addObserver(currentUser));
        return new RedirectView("/task/show/" + id);
    }

    @PostMapping("/task/{id}/unobserve")
    public RedirectView unsubscribe(@PathVariable int id) {
        tasks.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .ifPresent(t -> t.removeObserver(currentUser));
        return new RedirectView("/task/show/" + id);
    }

    /* ---------- MODELS ---------- */

    static class Comment {
        private final String author;
        private final String text;

        public Comment(String author, String text) {
            this.author = author;
            this.text = text;
        }

        public String getAuthor() { return author; }
        public String getText() { return text; }
    }

    static class Task {
        private static int counter = 1;
        private final int id = counter++;

        private String project;
        private String title;
        private String description;
        private String assignee;
        private String startDate;
        private String endDate;
        private String status = "Новая";
        private final String creator;

        private final List<Comment> comments = new ArrayList<>();
        private final List<String> watchers = new ArrayList<>();

        public Task(String project, String title, String description,
                    String assignee, String startDate, String endDate,
                    String creator) {
            this.project = project;
            this.title = title;
            this.description = description;
            this.assignee = assignee;
            this.startDate = startDate;
            this.endDate = endDate;
            this.creator = creator;
        }

        public int getId() { return id; }
        public String getProject() { return project; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getAssignee() { return assignee; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getStatus() { return status; }
        public String getCreator() { return creator; }
        public List<Comment> getComments() { return comments; }
        public List<String> getWatchers() { return watchers; }

        public void addObserver(String user) {
            if (!watchers.contains(user)) watchers.add(user);
        }

        public void removeObserver(String user) {
            watchers.remove(user);
        }
    }
}
