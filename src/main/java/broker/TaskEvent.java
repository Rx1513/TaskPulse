package broker;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {
    private Long taskId;
    private String type;
}
