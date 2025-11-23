package vn.edu.humg.olympic.api.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static vn.edu.humg.olympic.api.util.GenerateRandom.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import vn.edu.humg.olympic.api.model.Role;
import vn.edu.humg.olympic.api.model.User;
import vn.edu.humg.olympic.api.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        int n = generateNumber(15);
        for (int i = 0; i < n; ++i) {
            String email = generateRandomEmail();
            String passwordHash = generateRandomText();
            Role role = Role.STUDENT;

            User user =
                    User.builder()
                            .id(1L)
                            .email(email)
                            .passwordHash(passwordHash)
                            .role(role)
                            .build();

            when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            assertThat(userDetails).isNotNull();
            assertThat(userDetails.getUsername()).isEqualTo(email);
            assertThat(userDetails.getPassword()).isEqualTo(passwordHash);
            assertThat(userDetails.getAuthorities())
                    .extracting("authority")
                    .containsExactly(role.getAuthority());

            verify(userRepository).findByEmail(email);
            reset(userRepository);
        }
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenUserNotFound() {
        int n = generateNumber(15);
        for (int i = 0; i < n; ++i) {
            String email = generateRandomEmail();

            when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userDetailsService.loadUserByUsername(email))
                    .isInstanceOf(UsernameNotFoundException.class)
                    .hasMessage("Username not found!");

            verify(userRepository).findByEmail(email);
            reset(userRepository);
        }
    }
}
