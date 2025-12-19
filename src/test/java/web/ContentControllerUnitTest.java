package web;

import static org.assertj.core.api.Assertions.assertThat;

import database.TaskRepository;
import database.UserRepository;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.ModelAndView;
import tasks.Status;
import tasks.Task;
import tasks.TaskPreview;
import users.User;
import users.UserSearchDTO;

class ContentControllerUnitTest {

    private ContentController controller;
    private StubUserRepository userRepository;
    private StubTaskRepository taskRepository;
    private Principal principal;

    @BeforeEach
    void setup() {
        userRepository = new StubUserRepository();
        taskRepository = new StubTaskRepository();
        controller = new ContentController();
        controller.userRepository = userRepository;
        controller.taskRepository = taskRepository;
        principal = () -> "john";
        userRepository.current =
                User.builder().id(1L).name("john").email("john@mail").passwordHash("p").build();
    }

    @Test
    void tasksViewPopulatesModel() {
        Task t =
                Task.builder()
                        .id(10L)
                        .title("Task")
                        .project("Proj")
                        .status(Status.NEW)
                        .creator(userRepository.current)
                        .build();
        taskRepository.tasks.add(t);

        ModelAndView mv = controller.tasks(principal);
        assertThat(mv.getViewName()).isEqualTo("tasks");
        assertThat(mv.getModel().get("currentUser")).isEqualTo("john");
        assertThat((List<?>) mv.getModel().get("tasks")).hasSize(1);
    }

    @Test
    void searchUsersReturnsDtos() {
        userRepository.searchResult.add(userRepository.current);
        List<UserSearchDTO> result = controller.searchUsers("jo");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("john");
    }

    private static class StubUserRepository implements UserRepository {
        User current;
        List<User> searchResult = new ArrayList<>();

        @Override
        public Optional<User> findUserById(long id) {
            return Optional.ofNullable(current);
        }

        @Override
        public Optional<User> findUserByName(String username) {
            return Optional.ofNullable(current);
        }

        @Override
        public void addUser(User user) {}

        @Override
        public void verifyUser(User user, String password) {}

        @Override
        public database.UserExistenceResult checkUserExistence(User user) {
            return database.UserExistenceResult.NOT_FOUND;
        }

        @Override
        public List<User> searchUsers(String query, int limit) {
            return searchResult;
        }
    }

    private static class StubTaskRepository implements TaskRepository {
        List<Task> tasks = new ArrayList<>();

        @Override
        public Optional<Task> findTaskById(long id) {
            return tasks.stream().filter(t -> t.getId() == id).findFirst();
        }

        @Override
        public void addTask(Task task) {
            tasks.add(task);
        }

        @Override
        public void deleteTask(Task task) {
            tasks.remove(task);
        }

        @Override
        public void addUserToSubscriptionList(Task task, User subscriber) {}

        @Override
        public void removeUserFromSubscriptionList(Task task, User subscriber) {}

        @Override
        public void changeTask(Task old_task, Task new_task) {}

        @Override
        public void addComment(Task task, User commentator, String comment) {}

        @Override
        public List<TaskPreview> getTasksPreviewsByUser(User user) {
            return new ArrayList<>(tasks);
        }

        @Override
        public List<tasks.Comment> getCommentsForTask(Task task) {
            return List.of();
        }
    }
}
