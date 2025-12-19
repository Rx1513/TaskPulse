package email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import tasks.Comment;
import tasks.Status;
import tasks.Task;
import users.User;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock private JavaMailSender mailSender;
    @Mock private EmailTemplateService templateService;

    private EmailService service;

    @BeforeEach
    void setup() {
        service = new EmailService(mailSender, templateService);
    }

    @Test
    void sendNewTaskNotificationRendersAndSends() {
        Task task = buildTask();
        when(templateService.render(eq("mail/task-created.txt"), any(Map.class))).thenReturn("BODY");

        List<String> recipients = captureRecipients();

        service.sendNewTaskNotification(task);

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
        assertThat(recipients).containsExactlyInAnyOrder("sub1@mail", "sub2@mail");

        Map<String, Object> vars = captureVars("mail/task-created.txt");
        assertThat(vars.get("project")).isEqualTo("Project");
        assertThat(vars.get("task_title")).isEqualTo("Title");
        assertThat(vars.get("assignee_name")).isEqualTo("Assignee");
        assertThat(vars.get("task_url")).isEqualTo("http://localhost:8080/task/show/99");
    }

    @Test
    void sendCommentUpdateNotificationUsesLatestComment() {
        Task task = buildTask();
        Comment older =
                Comment.builder()
                        .commentator(task.getCreator())
                        .content("First")
                        .createdAt(Instant.now())
                        .task(task)
                        .build();
        Comment latest =
                Comment.builder()
                        .commentator(task.getAssignee())
                        .content("Latest")
                        .createdAt(Instant.now())
                        .task(task)
                        .build();
        task.getComments().add(older);
        task.getComments().add(latest);

        when(templateService.render(eq("mail/task-comment.txt"), any(Map.class))).thenReturn("BODY");

        List<String> recipients = captureRecipients();

        service.sendCommentUpdateNotification(task);

        assertThat(recipients).containsExactlyInAnyOrder("sub1@mail", "sub2@mail");

        Map<String, Object> vars = captureVars("mail/task-comment.txt");
        assertThat(vars.get("comment_text")).isEqualTo("Latest");
        assertThat(vars.get("commentator")).isEqualTo(task.getAssignee());
        assertThat(vars.get("status")).isEqualTo(task.getStatus().getDisplayName());
    }

    @Test
    void sendDeadlineNotificationRendersTemplate() {
        Task task = buildTask();
        when(templateService.render(eq("mail/task-deadline.txt"), any(Map.class))).thenReturn("BODY");

        captureRecipients();

        service.sendDeadlineNotification(task);

        Map<String, Object> vars = captureVars("mail/task-deadline.txt");
        assertThat(vars.get("task_description")).isEqualTo("Desc");
    }

    @Test
    void sendDeletedTaskNotificationIncludesEditor() {
        Task task = buildTask();
        User editor = User.builder().name("Editor").email("edit@mail").passwordHash("p").build();
        when(templateService.render(eq("mail/task-deleted.txt"), any(Map.class))).thenReturn("BODY");

        captureRecipients();

        service.sendDeletedTaskNotification(task, editor);

        Map<String, Object> vars = captureVars("mail/task-deleted.txt");
        assertThat(vars.get("editor_name")).isEqualTo("Editor");
    }

    @Test
    void sendEditedTaskNotificationIncludesEditorAndUrl() {
        Task task = buildTask();
        User editor = User.builder().name("Editor").email("edit@mail").passwordHash("p").build();
        when(templateService.render(eq("mail/task-updated.txt"), any(Map.class))).thenReturn("BODY");

        captureRecipients();

        service.sendEditedTaskNotification(task, editor);

        Map<String, Object> vars = captureVars("mail/task-updated.txt");
        assertThat(vars.get("editor_name")).isEqualTo("Editor");
        assertThat(vars.get("task_url")).isEqualTo("http://localhost:8080/task/show/99");
    }

    private Task buildTask() {
        User creator = User.builder().id(1L).name("Creator").email("creator@mail").passwordHash("p").build();
        User assignee = User.builder().id(2L).name("Assignee").email("assignee@mail").passwordHash("p").build();
        User sub1 = User.builder().id(3L).name("Sub1").email("sub1@mail").passwordHash("p").build();
        User sub2 = User.builder().id(4L).name("Sub2").email("sub2@mail").passwordHash("p").build();

        Task task =
                Task.builder()
                        .id(99L)
                        .project("Project")
                        .title("Title")
                        .status(Status.INWORK)
                        .creator(creator)
                        .assignee(assignee)
                        .description("Desc")
                        .start(LocalDate.of(2025, 1, 1))
                        .end(LocalDate.of(2025, 1, 2))
                        .subscriptionList(new HashSet<>())
                        .comments(new ArrayList<>())
                        .build();
        task.getSubscriptionList().addAll(Set.of(sub1, sub2));
        return task;
    }

    private List<String> captureRecipients() {
        List<String> recipients = new ArrayList<>();
        doAnswer(
                        invocation -> {
                            SimpleMailMessage msg = invocation.getArgument(0);
                            String[] to = msg.getTo();
                            if (to != null && to.length > 0) {
                                recipients.add(to[0]);
                            }
                            assertThat(msg.getText()).isEqualTo("BODY");
                            return null;
                        })
                .when(mailSender)
                .send(any(SimpleMailMessage.class));
        return recipients;
    }

    private Map<String, Object> captureVars(String template) {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        verify(templateService).render(eq(template), captor.capture());
        return captor.getValue();
    }
}
