package users;

import broker.TaskEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaskEventListener {

    @KafkaListener(topics = "task-events", groupId = "users-service")
    public void handle(TaskEvent event) {
        System.out.println("Получено событие из Kafka: " + event);
    }
}
