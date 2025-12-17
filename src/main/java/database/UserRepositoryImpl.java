package database;

import database.jpa.UserJpaRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import users.User;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Optional<User> findUserById(long id) {
        return userJpaRepository.findById(id);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userJpaRepository.findByEmail(email);
    }

    @Override
    public void addUser(User user, String password) {
        if (checkUserExistence(user)) {
            throw new org.springframework.dao.DataIntegrityViolationException(
                    "User already exists with id or email");
        }
        user.setPasswordHash(passwordEncoder.encode(password));
        userJpaRepository.save(user);
    }

    @Override
    public void verifyUser(User user, String password) throws AuthenticationException {
        Optional<User> persistedOpt =
                user.getId() != null
                        ? userJpaRepository.findById(user.getId())
                        : userJpaRepository.findByEmail(user.getEmail());

        User persisted = persistedOpt.orElseThrow(() -> new AuthenticationException("User not found"));

        if (!passwordEncoder.matches(password, persisted.getPasswordHash())) {
            throw new AuthenticationException("Invalid credentials");
        }
    }

    @Override
    public boolean checkUserExistence(User user) {
        if (user.getId() != null) {
            return userJpaRepository.existsById(user.getId());
        }
        if (user.getEmail() == null) {
            throw new EmptyResultDataAccessException("User id or email is required", 1);
        }
        return userJpaRepository.existsByEmail(user.getEmail());
    }
}
