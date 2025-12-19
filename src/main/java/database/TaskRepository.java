package database;

import tasks.Comment;
import tasks.Task;
import tasks.TaskPreview;
import users.User;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository {
    public Optional<Task> findTaskById(long id);
    public void addTask(Task task);
    public void deleteTask(Task task);
    public void addUserToSubscriptionList(Task task,User subscriber);
    public void removeUserFromSubscriptionList(Task task,User subscriber);
    public void changeTask(Task old_task,Task new_task);
    public void addComment(Task task, User commentator, String comment);
    public List<TaskPreview> getTasksPreviewsByUser(User user);
    public List<Comment> getCommentsForTask(Task task);
}
