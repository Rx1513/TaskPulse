package tasks;

import users.User;

import java.util.List;

public class Task extends TaskPreview {
    private User creator;
    private String description;
    private List<User> subscription_list;
    private List<Comment> comments;
}
