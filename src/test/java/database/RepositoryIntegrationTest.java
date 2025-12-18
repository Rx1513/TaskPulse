package database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import database.jpa.CommentJpaRepository;
import database.jpa.TaskJpaRepository;
import database.jpa.UserJpaRepository;
import java.util.Date;
import java.util.List;
import javax.naming.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tasks.Comment;
import tasks.Status;
import tasks.Task;
import tasks.TaskPreview;
import users.User;
import web.HttpServer;

@SpringBootTest(
        classes = HttpServer.class,
        properties = {"spring.test.database.replace=NONE"})
@Testcontainers
@TestInstance(Lifecycle.PER_CLASS)
@Transactional
class RepositoryIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine").withDatabaseName("taskpulse");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired private UserRepository userRepository;

    @Autowired private TaskRepository taskRepository;

    @Autowired private UserJpaRepository userJpaRepository;

    @Autowired private TaskJpaRepository taskJpaRepository;

    @Autowired private CommentJpaRepository commentJpaRepository;

    @BeforeEach
    void cleanDatabase() {
        commentJpaRepository.deleteAll();
        taskJpaRepository.deleteAll();
        userJpaRepository.deleteAll();
    }

    @Test
    void addAndVerifyUser() throws Exception {
        User user = User.builder().name("Alice").email("alice@example.com").build();
        userRepository.addUser(user, "pa55word");

        assertThat(user.getId()).isNotNull();

        User persisted = userRepository.findUserById(user.getId()).orElseThrow();
        assertThat(persisted.getEmail()).isEqualTo("alice@example.com");
        assertThat(persisted.getPasswordHash()).isNotEqualTo("pa55word");

        userRepository.verifyUser(persisted, "pa55word");
        assertThatThrownBy(() -> userRepository.verifyUser(persisted, "bad"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void duplicateEmailThrowsException() {
        User user = User.builder().name("Bob").email("bob@example.com").build();
        userRepository.addUser(user, "secret");

        User duplicate = User.builder().name("Bobby").email("bob@example.com").build();
        assertThatThrownBy(() -> userRepository.addUser(duplicate, "another"))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void searchUsersReturnsMatchesAndHonorsLimit() {
        User alice = User.builder().name("Alice").email("alice@example.com").build();
        userRepository.addUser(alice, "p1");

        User albert = User.builder().name("Albert").email("albert@example.com").build();
        userRepository.addUser(albert, "p2");

        User bob = User.builder().name("Bob").email("builder@tools.com").build();
        userRepository.addUser(bob, "p3");

        assertThat(userRepository.searchUsers("al", 10))
                .extracting(User::getEmail)
                .containsExactlyInAnyOrder("alice@example.com", "albert@example.com");

        assertThat(userRepository.searchUsers("builder", 1)).hasSize(1);
    }

    @Test
    void searchUsersReturnsEmptyOnBlankOrNoMatch() {
        User alice = User.builder().name("Alice").email("alice@example.com").build();
        userRepository.addUser(alice, "p1");

        assertThat(userRepository.searchUsers("   ", 5)).isEmpty();
        assertThat(userRepository.searchUsers("nomatch", 5)).isEmpty();
    }

    @Test
    void taskLifecycleCoversUpdatesAndRelations() {
        User creator = User.builder().name("Creator").email("creator@task.com").build();
        userRepository.addUser(creator, "cpass");

        User performer = User.builder().name("Performer").email("performer@task.com").build();
        userRepository.addUser(performer, "ppass");

        User subscriber = User.builder().name("Subscriber").email("subscriber@task.com").build();
        userRepository.addUser(subscriber, "spass");

        Task task = new Task();
        task.setTitle("Initial");
        task.setProject("TaskPulse");
        task.setStatus(Status.NEW);
        task.setCreator(creator);
        task.setPerformer(performer);
        task.setDescription("Old description");
        task.setStart(new Date());
        task.setEnd(new Date(System.currentTimeMillis() + 3_600_000));

        taskRepository.addTask(task);
        Long taskId = task.getId();

        Task persisted = taskRepository.findTaskById(taskId).orElseThrow();
        assertThat(persisted.getStatus()).isEqualTo(Status.NEW);
        assertThat(persisted.getCreator().getId()).isEqualTo(creator.getId());

        taskRepository.changeTitle(persisted, "Updated title");
        taskRepository.changeDescription(persisted, "New description");
        taskRepository.changeProject(persisted, "Platform");
        taskRepository.changeAssignee(persisted, subscriber);
        taskRepository.changeStartDate(persisted, new Date(1_000));
        taskRepository.changeEndDate(persisted, new Date(2_000));

        Task updated = taskRepository.findTaskById(taskId).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.getDescription()).isEqualTo("New description");
        assertThat(updated.getProject()).isEqualTo("Platform");
        assertThat(updated.getPerformer().getId()).isEqualTo(subscriber.getId());

        taskRepository.addUserToSubscriptionList(updated, performer);
        Task afterSubscription = taskRepository.findTaskById(taskId).orElseThrow();
        assertThat(afterSubscription.getSubscriptionList()).extracting(User::getId).contains(performer.getId());

        taskRepository.addComment(afterSubscription, performer, "Nice work");
        taskRepository.addComment(afterSubscription, creator, "Auto message");
        List<Comment> comments = taskRepository.getCommentsForTask(afterSubscription);
        assertThat(comments).hasSize(2);

        List<TaskPreview> relatedToPerformer = taskRepository.getTasksPreviewsByUser(performer);
        assertThat(relatedToPerformer).extracting(TaskPreview::getId).contains(taskId);
    }
}
