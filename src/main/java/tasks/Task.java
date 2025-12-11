package tasks;

import users.User;

import java.util.Date;
import java.util.List;

public class Task extends TaskPreview {
    private User creator;
    private List<User> subscription_list;
    private String description;
}
