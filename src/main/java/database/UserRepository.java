package database;

import users.User;

import javax.naming.AuthenticationException;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
    Optional<User> findUserById(long id);
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByName(String username);
    void addUser(User user);
    void verifyUser(User user, String password) throws AuthenticationException;
    UserExistenceResult checkUserExistence(User user);
    List<User> searchUsers(String query, int limit);
}
