package email;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import users.User;

@Getter
@AllArgsConstructor
public class EmailNotificationEvent {
    private Set<User> recipients;
    private String subject;
    private String body;
}
