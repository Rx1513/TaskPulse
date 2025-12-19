package tasks;

import database.TaskRepository;
import email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import users.User;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    @Autowired
    private final TaskRepository taskRepository;

    private final EmailService emailService;

    public void createTask(
            String project,
            String title,
            String description,
            User creator,
            User assignee,
            LocalDate startDate,
            LocalDate endDate,
            Status status
    ) {

        Task task = Task.builder()
                .creator(creator)
                .description(description)
                .title(title)
                .project(project)
                .assignee(assignee)
                .start(startDate)
                .end(endDate)
                .status(status)
                .subscriptionList(new HashSet<>())
                .build();
        Set users = task.getSubscriptionList();
        users.add(creator);
        users.add(assignee);
        task.setSubscriptionList(users);
        taskRepository.addTask(task);
        emailService.sendNewTaskNotification(task);
    }

    public void deleteTaskById(long id, User editor) {
        Optional<Task> task = taskRepository.findTaskById(id);
        if (task.isPresent()) {
            taskRepository.deleteTask(task.get());
            emailService.sendDeletedTaskNotification(task.get(), editor);
        }
    }

    public void changeTaskById(long id,
                               String project,
                               String title,
                               String description,
                               User assignee,
                               LocalDate startDate,
                               LocalDate endDate,
                               Status status,
                               User editor) {
        Optional<Task> taskToReplace = taskRepository.findTaskById(id);
        if (taskToReplace.isEmpty()) {
            throw new EmptyResultDataAccessException("Изменяемая задача недоступна!", 1);
        }
        Task newTask = Task.builder()
                .id(taskToReplace.get().getId())
                .creator(taskToReplace.get().getCreator())
                .description(description)
                .title(title)
                .project(project)
                .assignee(assignee)
                .start(startDate)
                .end(endDate)
                .status(status)
                .subscriptionList(taskToReplace.get().getSubscriptionList())
                .build();

        taskRepository.changeTask(taskToReplace.get(),newTask);
        emailService.sendEditedTaskNotification(taskToReplace.get(), editor);
    }

    public void addCommentToTaskById(long id, User user,
                                     String comment) {
        Optional<Task> task = taskRepository.findTaskById(id);
        if (task.isEmpty()) {
            throw new EmptyResultDataAccessException("Изменяемая задача недоступна!", 1);
        }
        taskRepository.addComment(task.get(),user,comment);
        emailService.sendCommentUpdateNotification(task.get());
    }

    public void addToSubscriptionByTaskId(long id, User user) {
        Optional<Task> task = taskRepository.findTaskById(id);
        if (task.isEmpty()) {
            throw new EmptyResultDataAccessException("Изменяемая задача недоступна!", 1);
        }
        taskRepository.addUserToSubscriptionList(task.get(),user);
    }

    public void removeFromSubscriptionByTaskId(long id, User user) {
        Optional<Task> task = taskRepository.findTaskById(id);
        if (task.isEmpty()) {
            throw new EmptyResultDataAccessException("Изменяемая задача недоступна!", 1);
        }
        taskRepository.removeUserFromSubscriptionList(task.get(),user);
    }
}

