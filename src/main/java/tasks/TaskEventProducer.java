package tasks;

import broker.TaskEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskEventProducer {

    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public void sendCreated(Long taskId) {
        kafkaTemplate.send("task-events", new TaskEvent(taskId, "CREATED"));
    }
}
