package database;

import database.jpa.UserJpaRepository;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.naming.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
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
    public Optional<User> findUserByName(String name) { return userJpaRepository.findByName(name); }

    @Override
    public void addUser(User user) {
        UserExistenceResult result =  checkUserExistence(user);

        switch (result) {
            case FOUND_BY_ID -> throw new org.springframework.dao.DataIntegrityViolationException("Пользователь уже зарестрирован! (ID = " + user.getId() + ")");
            case FOUND_BY_NAME -> throw new org.springframework.dao.DataIntegrityViolationException("Пользователь уже зарестрирован! (Имя " + user.getName() + " уже существует!)");
            case FOUND_BY_EMAIL -> throw new org.springframework.dao.DataIntegrityViolationException("Пользователь уже зарестрирован! (Почта " + user.getEmail() + " уже привязана!)");
        }

        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
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
    public UserExistenceResult checkUserExistence(User user) {
        if (user.getId() != null) {
            if (userJpaRepository.existsById(user.getId())) {
                return UserExistenceResult.FOUND_BY_ID;
            }
        }

        if (user.getName() != null) {
            if (userJpaRepository.existsByName(user.getName())) {
                return UserExistenceResult.FOUND_BY_NAME;
            }
        }

        if (user.getEmail() != null) {
            if (userJpaRepository.existsByEmail(user.getEmail())) {
                return UserExistenceResult.FOUND_BY_EMAIL;
            }
        } else {
            throw new EmptyResultDataAccessException("User id, name or email is required", 1);
        }
        return UserExistenceResult.NOT_FOUND;
    }

    @Override
    public List<User> searchUsers(String query, int limit) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }
        int pageSize = limit > 0 ? limit : 10;
        return userJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                query, query, PageRequest.of(0, pageSize));
    }
}
