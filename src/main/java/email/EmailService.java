package email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tasks.Comment;
import tasks.Task;
import users.User;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService emailTemplateService;
    private final static String DOMAIN = "localhost";
    private final static String PORT = "8080";

    public void sendNewTaskNotification(Task task) {
        String subject =  "[" + task.getProject() + "] Новая задача: " + task.getTitle();
        Map<String, Object> vars = Map.of(
                "project", task.getProject(),
                "task_title", task.getTitle(),
                "author_name", task.getCreator().getName(),
                "assignee_name", task.getAssignee().getName(),
                "task_description", task.getDescription(),
                "due_date", task.getStart() + "-" + task.getEnd(),
                "task_url", "http://" + DOMAIN + ":" + PORT +  "/task/show/" + task.getId()
        );
        String body = emailTemplateService.render(
                "mail/task-created.txt",
                vars
        );
        sendEmailNotification(task.getSubscriptionList(), subject, body);
    }

    public void sendCommentUpdateNotification(Task task) {
        List<Comment> completeCommentsList = task.getComments();
        Comment comment = completeCommentsList.get(completeCommentsList.size() - 1);

        String subject =  "[" + task.getProject() + "] Новый комментарий к задаче: " + task.getTitle();
        Map<String, Object> vars = Map.of(
                "project", task.getProject(),
                "task_title", task.getTitle(),
                "author_name", task.getCreator().getName(),
                "assignee_name", task.getAssignee().getName(),
                "status", task.getStatus().getDisplayName(),
                "due_date", task.getStart() + "-" + task.getEnd(),
                "task_description", task.getDescription(),
                "commentator", comment.getCommentator(),
                "comment_text", comment.getContent(),
                "task_url", "http://" + DOMAIN + ":" + PORT +  "/task/show/" + task.getId()
        );
        String body = emailTemplateService.render(
                "mail/task-comment.txt",
                vars
        );
        sendEmailNotification(task.getSubscriptionList(), subject, body);
    }

    public void sendDeadlineNotification(Task task) {
        SimpleMailMessage message = new SimpleMailMessage();
        String subject =  "[" + task.getProject() + "] Приближается конец срока по задаче: " + task.getTitle();
        Map<String, Object> vars = Map.of(
                "project", task.getProject(),
                "task_title", task.getTitle(),
                "author_name", task.getCreator().getName(),
                "assignee_name", task.getAssignee().getName(),
                "status", task.getStatus().getDisplayName(),
                "due_date", task.getStart() + "-" + task.getEnd(),
                "task_description", task.getDescription(),
                "task_url", "http://" + DOMAIN + ":" + PORT +  "/task/show/" + task.getId()
        );
        String body = emailTemplateService.render(
                "mail/task-deadline.txt",
                vars
        );
        sendEmailNotification(task.getSubscriptionList(), subject, body);
    }

    public void sendDeletedTaskNotification(Task task) {
        String subject = "[" + task.getProject() + "] Задача была удалена: " + task.getTitle();
        Map<String, Object> vars = Map.of(
                "project", task.getProject(),
                "task_title", task.getTitle(),
                "author_name", task.getCreator().getName(),
                "assignee_name", task.getAssignee().getName(),
                "status", task.getStatus().getDisplayName(),
                "due_date", task.getStart() + "-" + task.getEnd(),
                "task_description", task.getDescription()
        );
        String body = emailTemplateService.render(
                "mail/task-deleted.txt",
                vars
        );
        sendEmailNotification(task.getSubscriptionList(), subject, body);
    }

    public void sendEditedTaskNotification(Task task) {
        String subject = "[" + task.getProject() + "] Задача была изменена: " + task.getTitle();
        Map<String, Object> vars = Map.of(
                "project", task.getProject(),
                "task_title", task.getTitle(),
                "author_name", task.getCreator().getName(),
                "assignee_name", task.getAssignee().getName(),
                "status", task.getStatus().getDisplayName(),
                "due_date", task.getStart() + "-" + task.getEnd(),
                "task_description", task.getDescription(),
                "task_url", "http://" + DOMAIN + ":" + PORT +  "/task/show/" + task.getId()
        );
        String body = emailTemplateService.render(
                "mail/task-updated.txt",
                vars
        );
        sendEmailNotification(task.getSubscriptionList(), subject, body);
    }

    private void sendEmailNotification(Set<User> subscriberList, String subject ,String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("taskpulse@yandex.ru");
        message.setSubject(subject);
        message.setText(body);
        for (User subscriber : subscriberList) {
            message.setTo(subscriber.getEmail());
            mailSender.send(message);
        }
    }
}
