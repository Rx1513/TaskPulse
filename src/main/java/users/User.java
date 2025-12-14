package users;

import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@AllArgsConstructor
public class User {
    private final long id;
    private final String name;
    private final String email;
}
