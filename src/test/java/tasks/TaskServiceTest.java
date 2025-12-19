package tasks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import database.TaskRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import users.User;

class TaskServiceTest {

    private TaskService taskService;
    private InMemoryTaskRepository repo;
    private StubEmailService emailService;
    private User creator;
    private User assignee;

    @BeforeEach
    void setup() {
        repo = new InMemoryTaskRepository();
        emailService = new StubEmailService();
        taskService = new TaskService(repo, emailService);
        creator = User.builder().id(1L).name("creator").email("c@mail").passwordHash("p").build();
        assignee = User.builder().id(2L).name("assignee").email("a@mail").passwordHash("p").build();
    }

    @Test
    void createTaskAddsSubscribers() {
        taskService.createTask(
                "Proj",
                "Title",
                "Desc",
                creator,
                assignee,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                Status.NEW);

        assertThat(repo.savedTasks).hasSize(1);
        Task saved = repo.savedTasks.get(0);
        Set<User> subs = saved.getSubscriptionList();
        assertThat(subs).extracting(User::getId).containsExactlyInAnyOrder(1L, 2L);
        assertThat(emailService.newTaskSent).isEqualTo(1);
    }

    @Test
    void deleteTaskDoesNothingWhenMissing() {
        taskService.deleteTaskById(10L, creator);
        assertThat(repo.deleted).isEqualTo(0);
        assertThat(emailService.deletedTaskSent).isEqualTo(0);
    }

    @Test
    void changeTaskThrowsWhenMissing() {
        assertThatThrownBy(
                        () ->
                                taskService.changeTaskById(
                                        5L,
                                        "p",
                                        "t",
                                        "d",
                                        assignee,
                                        LocalDate.now(),
                                        LocalDate.now(),
                                        Status.DONE,
                                        creator))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    @Test
    void addCommentThrowsWhenMissing() {
        assertThatThrownBy(() -> taskService.addCommentToTaskById(3L, creator, "c"))
                .isInstanceOf(org.springframework.dao.EmptyResultDataAccessException.class);
    }

    private static class InMemoryTaskRepository implements TaskRepository {
        List<Task> savedTasks = new ArrayList<>();
        int deleted = 0;

        @Override
        public Optional<Task> findTaskById(long id) {
            return savedTasks.stream().filter(t -> t.getId() == id).findFirst();
        }

        @Override
        public void addTask(Task task) {
            if (task.getId() == null) {
                task.setId((long) (savedTasks.size() + 1));
            }
            savedTasks.add(task);
        }

        @Override
        public void deleteTask(Task task) {
            deleted++;
            savedTasks.remove(task);
        }

        @Override
        public void addUserToSubscriptionList(Task task, User subscriber) {}

        @Override
        public void removeUserFromSubscriptionList(Task task, User subscriber) {}

        @Override
        public void changeTask(Task old_task, Task new_task) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void addComment(Task task, User commentator, String comment) {}

        @Override
        public List<TaskPreview> getTasksPreviewsByUser(User user) {
            return List.of();
        }

        @Override
        public List<tasks.Comment> getCommentsForTask(Task task) {
            return List.of();
        }
    }

    private static class StubEmailService extends email.EmailService {
        int newTaskSent = 0;
        int deletedTaskSent = 0;

        StubEmailService() {
            super(null, null);
        }

        @Override
        public void sendNewTaskNotification(Task task) {
            newTaskSent++;
        }

        @Override
        public void sendDeletedTaskNotification(Task task, User editor) {
            deletedTaskSent++;
        }
    }
}
