package email;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailEventProducer {
    private final KafkaTemplate<String, EmailNotificationEvent> kafkaTemplate;

    public void sendEmail(EmailNotificationEvent event) {
        kafkaTemplate.send("email-events", event);
    }
}
