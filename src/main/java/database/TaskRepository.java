package database;

import tasks.Comment;
import tasks.Task;
import tasks.TaskPreview;
import users.User;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository {
    public Optional<Task> findTaskById(long id);
    public void addTask(Task task);
    public void addUserToSubscriptionList(Task task,User subscriber);
    public void changeTask(Task old_task,Task new_task);
    public void changeDescription(Task task, String description);
    public void changeAssignee(Task task, User assignee);
    public void changeCreator(Task task, User assignee);
    public void changeStartDate(Task task, OffsetDateTime start);
    public void changeEndDate(Task task, OffsetDateTime end);
    public void changeTitle(Task task, String title);
    public void changeProject(Task task, String project);
    public void addComment(Task task, User commentator, String comment);
    public List<TaskPreview> getTasksPreviewsByUser(User user);
    public List<Comment> getCommentsForTask(Task task);
}
