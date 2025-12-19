package auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import database.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import users.User;

@ExtendWith(MockitoExtension.class)
class AuthUserDetailsServiceTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final AuthUserDetailsService service = new AuthUserDetailsService(userRepository);

    @Test
    void loadsUserAndBuildsUserDetails() {
        User user = User.builder().id(1L).name("john").email("john@mail").passwordHash("pwd").build();
        given(userRepository.findUserByName("john")).willReturn(Optional.of(user));

        UserDetails details = service.loadUserByUsername("john");

        assertThat(details.getUsername()).isEqualTo("john");
        assertThat(details.getPassword()).isEqualTo("pwd");
        assertThat(details.getAuthorities()).extracting(Object::toString).contains("ROLE_USER");
    }

    @Test
    void throwsWhenUserNotFound() {
        given(userRepository.findUserByName("missing")).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
