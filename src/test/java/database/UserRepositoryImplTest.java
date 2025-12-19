package database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import database.jpa.UserJpaRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import users.User;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock private UserJpaRepository userJpaRepository;

    private UserRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new UserRepositoryImpl(userJpaRepository);
    }

    @Test
    void addUserEncodesPasswordAndSaves() {
        given(userJpaRepository.existsByName(any())).willReturn(false);
        given(userJpaRepository.existsByEmail(any())).willReturn(false);

        User user = User.builder().name("Alice").email("alice@mail").passwordHash("plain").build();
        repository.addUser(user);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userJpaRepository).save(captor.capture());
        User saved = captor.getValue();
        assertThat(saved.getPasswordHash()).isNotEqualTo("plain");
        assertThat(new BCryptPasswordEncoder().matches("plain", saved.getPasswordHash())).isTrue();
    }

    @Test
    void addUserFailsOnDuplicateEmail() {
        given(userJpaRepository.existsByName(any())).willReturn(false);
        given(userJpaRepository.existsByEmail("dup@mail")).willReturn(true);

        User user = User.builder().name("Dup").email("dup@mail").passwordHash("plain").build();
        assertThatThrownBy(() -> repository.addUser(user)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void checkUserExistenceFindsByIdThenNameThenEmail() {
        User byId = User.builder().id(10L).build();
        given(userJpaRepository.existsById(10L)).willReturn(true);
        assertThat(repository.checkUserExistence(byId)).isEqualTo(UserExistenceResult.FOUND_BY_ID);

        User byName = User.builder().name("Bob").build();
        given(userJpaRepository.existsByName("Bob")).willReturn(true);
        assertThat(repository.checkUserExistence(byName)).isEqualTo(UserExistenceResult.FOUND_BY_NAME);

        User byEmail = User.builder().email("b@mail").build();
        given(userJpaRepository.existsByEmail("b@mail")).willReturn(true);
        assertThat(repository.checkUserExistence(byEmail)).isEqualTo(UserExistenceResult.FOUND_BY_EMAIL);
    }

    @Test
    void checkUserExistenceThrowsWhenNoIdentifiers() {
        User empty = new User();
        assertThatThrownBy(() -> repository.checkUserExistence(empty))
                .isInstanceOf(EmptyResultDataAccessException.class);
    }

    @Test
    void searchUsersReturnsEmptyOnBlank() {
        assertThat(repository.searchUsers("   ", 5)).isEmpty();
    }

    @Test
    void searchUsersDelegatesToRepository() {
        List<User> expected = List.of(User.builder().name("A").build());
        given(userJpaRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        eq("al"), eq("al"), any(PageRequest.class)))
                .willReturn(expected);

        List<User> result = repository.searchUsers("al", -1);

        assertThat(result).isEqualTo(expected);
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(userJpaRepository)
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(eq("al"), eq("al"), captor.capture());
        assertThat(captor.getValue().getPageSize()).isEqualTo(10); // default when limit invalid
    }
}
