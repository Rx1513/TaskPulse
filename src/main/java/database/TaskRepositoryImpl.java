package database;

import database.jpa.CommentJpaRepository;
import database.jpa.TaskJpaRepository;
import database.jpa.UserJpaRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;
import tasks.Comment;
import tasks.Status;
import tasks.Task;
import tasks.TaskPreview;
import users.User;

@Repository
@Transactional
public class TaskRepositoryImpl implements TaskRepository {
    private final TaskJpaRepository taskJpaRepository;
    private final CommentJpaRepository commentJpaRepository;
    private final UserJpaRepository userJpaRepository;

    public TaskRepositoryImpl(
            TaskJpaRepository taskJpaRepository,
            CommentJpaRepository commentJpaRepository,
            UserJpaRepository userJpaRepository) {
        this.taskJpaRepository = taskJpaRepository;
        this.commentJpaRepository = commentJpaRepository;
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<Task> findTaskById(long id) {
        return taskJpaRepository.findById(id);
    }

    @Override
    public void addTask(Task task) {
        requireUser(task.getCreator());
        if (task.getPerformer() != null) {
            requireUser(task.getPerformer());
        }
        if (task.getStatus() == null) {
            task.setStatus(Status.NEW);
        }
        taskJpaRepository.save(task);
    }

    @Override
    public void addUserToSubscriptionList(Task task, User subscriber) {
        Task persistedTask = requireTask(task);
        User persistedUser = requireUser(subscriber);
        persistedTask.getSubscriptionList().add(persistedUser);
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changeTask(Task old_task, Task new_task) {
        Task persistedTask = requireTask(old_task);
        copyTaskFields(persistedTask, new_task);
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changeDescription(Task task, String description) {
        Task persistedTask = requireTask(task);
        persistedTask.setDescription(description);
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changePerformer(Task task, User performer) {
        Task persistedTask = requireTask(task);
        persistedTask.setPerformer(requireUser(performer));
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changeCreator(Task task, User creator) {
        Task persistedTask = requireTask(task);
        persistedTask.setCreator(requireUser(creator));
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changeStartDate(Task task, java.util.Date start) {
        Task persistedTask = requireTask(task);
        persistedTask.setStart(start);
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changeEndDate(Task task, java.util.Date end) {
        Task persistedTask = requireTask(task);
        persistedTask.setEnd(end);
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changeTitle(Task task, String title) {
        Task persistedTask = requireTask(task);
        persistedTask.setTitle(title);
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void changeProject(Task task, String project) {
        Task persistedTask = requireTask(task);
        persistedTask.setProject(project);
        taskJpaRepository.save(persistedTask);
    }

    @Override
    public void addComment(Task task, User commentator, String comment) {
        Task persistedTask = requireTask(task);
        User persistedUser = commentator != null ? requireUser(commentator) : null;
        Comment newComment =
                Comment.builder()
                        .task(persistedTask)
                        .commentator(persistedUser)
                        .content(comment)
                        .createdAt(Instant.now())
                        .build();
        commentJpaRepository.save(newComment);
        persistedTask.getComments().add(newComment);
    }

    @Override
    public List<TaskPreview> getTasksPreviewsByUser(User user) {
        User persistedUser = requireUser(user);
        Set<Task> uniqueTasks = new LinkedHashSet<>();
        uniqueTasks.addAll(taskJpaRepository.findByPerformer(persistedUser));
        uniqueTasks.addAll(taskJpaRepository.findByCreator(persistedUser));
        uniqueTasks.addAll(taskJpaRepository.findDistinctBySubscriptionListContaining(persistedUser));
        return new ArrayList<>(uniqueTasks);
    }

    @Override
    public List<Comment> getCommentsForTask(Task task) {
        Task persistedTask = requireTask(task);
        return commentJpaRepository.findByTask(persistedTask);
    }

    private Task requireTask(Task task) {
        Long id = task.getId();
        if (id == null) {
            throw new EmptyResultDataAccessException("Task id is required", 1);
        }
        return taskJpaRepository
                .findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("Task not found: " + id, 1));
    }

    private User requireUser(User user) {
        Long id = user.getId();
        if (id == null) {
            throw new EmptyResultDataAccessException("User id is required", 1);
        }
        return userJpaRepository
                .findById(id)
                .orElseThrow(() -> new EmptyResultDataAccessException("User not found: " + id, 1));
    }

    private void copyTaskFields(Task persistedTask, Task newTask) {
        if (newTask.getTitle() != null) {
            persistedTask.setTitle(newTask.getTitle());
        }
        if (newTask.getProject() != null) {
            persistedTask.setProject(newTask.getProject());
        }
        if (newTask.getStatus() != null) {
            persistedTask.setStatus(newTask.getStatus());
        }
        persistedTask.setStart(newTask.getStart());
        persistedTask.setEnd(newTask.getEnd());
        persistedTask.setDescription(newTask.getDescription());

        if (newTask.getPerformer() != null) {
            persistedTask.setPerformer(requireUser(newTask.getPerformer()));
        } else {
            persistedTask.setPerformer(null);
        }

        if (newTask.getCreator() != null) {
            persistedTask.setCreator(requireUser(newTask.getCreator()));
        }

        persistedTask.getSubscriptionList().clear();
        if (newTask.getSubscriptionList() != null) {
            newTask
                    .getSubscriptionList()
                    .forEach(subscriber -> persistedTask.getSubscriptionList().add(requireUser(subscriber)));
        }
    }
}
