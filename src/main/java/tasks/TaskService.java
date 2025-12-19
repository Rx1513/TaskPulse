package tasks;

import database.TaskRepository;
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
        users.add(assignee);
        task.setSubscriptionList(users);
        taskRepository.addTask(task);
    }

    public void deleteTaskById(long id) {
        Optional<Task> task = taskRepository.findTaskById(id);
        task.ifPresent(taskRepository::deleteTask);
    }

    public void changeTaskById(long id,
                               String project,
                               String title,
                               String description,
                               User assignee,
                               LocalDate startDate,
                               LocalDate endDate,
                               Status status) {
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
    }

    public void addCommentToTaskById(long id, User user,
                                     String comment) {
        Optional<Task> task = taskRepository.findTaskById(id);
        if (task.isEmpty()) {
            throw new EmptyResultDataAccessException("Изменяемая задача недоступна!", 1);
        }
        taskRepository.addComment(task.get(),user,comment);
    }
}

