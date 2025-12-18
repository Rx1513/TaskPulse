package tasks;

import database.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
}

