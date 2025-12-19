package email;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailEventListener {
    private final EmailService emailService;

    @KafkaListener(topics = "email-events", groupId = "email-service")
    public void handleEmailEvent(EmailNotificationEvent event) {
        emailService.sendEmailNotification(
                event.getRecipients(),
                event.getSubject(),
                event.getBody()
        );
    }
}
