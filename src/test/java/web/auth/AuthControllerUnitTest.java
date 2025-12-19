package web.auth;

import static org.assertj.core.api.Assertions.assertThat;

import database.UserRepository;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import users.User;

class AuthControllerUnitTest {

    private AuthController controller;
    private StubUserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository = new StubUserRepository();
        controller = new AuthController();
        controller.userRepository = userRepository;
    }

    @Test
    void registerSuccessRedirects() {
        Model model = new ConcurrentModel();
        String view = controller.register("John", "john@example.com", "pwd", model);
        assertThat(view).isEqualTo("redirect:/auth/login");
        assertThat(userRepository.saved.get()).isTrue();
    }

    @Test
    void registerReturnsRegisterOnError() {
        userRepository.fail = true;
        Model model = new ConcurrentModel();
        String view = controller.register("Bad Name!", "bad-email", "pwd", model);
        assertThat(view).isEqualTo("register");
        assertThat(model.getAttribute("error")).isNotNull();
    }

    private static class StubUserRepository implements UserRepository {
        AtomicBoolean saved = new AtomicBoolean(false);
        boolean fail = false;

        @Override
        public java.util.Optional<User> findUserById(long id) {
            return java.util.Optional.empty();
        }

        @Override
        public java.util.Optional<User> findUserByName(String username) {
            return java.util.Optional.empty();
        }

        @Override
        public void addUser(User user) {
            if (fail) {
                throw new RuntimeException("fail");
            }
            saved.set(true);
        }

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
}
