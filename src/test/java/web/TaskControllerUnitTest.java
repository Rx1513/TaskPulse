package web;

import static org.assertj.core.api.Assertions.assertThat;

import database.UserRepository;
import java.security.Principal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.RedirectView;
import tasks.Status;
import tasks.TaskService;
import users.User;

class TaskControllerUnitTest {

    private TaskController controller;
    private StubUserRepository userRepository;
    private CapturingTaskService taskService;
    private Principal principal;

    @BeforeEach
    void setup() {
        userRepository = new StubUserRepository();
        taskService = new CapturingTaskService();
        controller = new TaskController(taskService);
        controller.userRepository = userRepository;
        userRepository.creator = User.builder().id(1L).name("creator").email("c@mail").passwordHash("p").build();
        userRepository.assignee = User.builder().id(2L).name("assignee").email("a@mail").passwordHash("p").build();
        principal = () -> "creator";
    }

    @Test
    void createTaskRedirectsAndInvokesService() {
        RedirectView view =
                controller.createTask(
                        "Proj",
                        "Title",
                        "Desc",
                        "assignee",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        principal);

        assertThat(view.getUrl()).isEqualTo("/tasks");
        assertThat(taskService.calledCreate).isTrue();
        assertThat(taskService.lastStatus).isEqualTo(Status.NEW);
    }

    private static class StubUserRepository implements UserRepository {
        User creator;
        User assignee;

        @Override
        public Optional<User> findUserById(long id) {
            return Optional.empty();
        }

        @Override
        public Optional<User> findUserByName(String username) {
            if ("creator".equals(username)) {
                return Optional.ofNullable(creator);
            }
            if ("assignee".equals(username)) {
                return Optional.ofNullable(assignee);
            }
            return Optional.empty();
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
        public java.util.List<User> searchUsers(String query, int limit) {
            return java.util.List.of();
        }
    }

    private static class CapturingTaskService extends TaskService {
        boolean calledCreate = false;
        Status lastStatus;

        public CapturingTaskService() {
            super(null);
        }

        @Override
        public void createTask(
                String project,
                String title,
                String description,
                User creator,
                User assignee,
                LocalDate startDate,
                LocalDate endDate,
                Status status) {
            calledCreate = true;
            lastStatus = status;
        }
    }
}
