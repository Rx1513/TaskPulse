package database;

import users.User;

import javax.naming.AuthenticationException;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    public Optional<User> findUserById(long id);
    public void addUser(User user, String password) throws RuntimeException;
    public void verifyUser(User user, String password) throws AuthenticationException;
    public boolean checkUserExistence(User user);
}
