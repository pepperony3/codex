package com.taskboard.service;

import com.taskboard.config.JwtUtil;
import com.taskboard.dto.AuthResponse;
import com.taskboard.dto.LoginRequest;
import com.taskboard.dto.RegisterRequest;
import com.taskboard.model.User;
import com.taskboard.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    private AuthService authService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setEmail("new@test.com");
        req.setPassword("password123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken("newuser")).thenReturn("jwt-token-123");

        AuthResponse resp = authService.register(req);

        assertEquals("jwt-token-123", resp.getToken());
        assertEquals("newuser", resp.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowWhenUsernameTaken() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("existing");
        req.setEmail("e@test.com");
        req.setPassword("password");

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(req));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrowWhenEmailTaken() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("user");
        req.setEmail("taken@test.com");
        req.setPassword("password");

        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.existsByEmail("taken@test.com")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> authService.register(req));
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsValid() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user");
        req.setPassword("correct");

        User user = new User("user", "u@test.com", passwordEncoder.encode("correct"));
        user.setId(1L);

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken("user")).thenReturn("jwt-token-456");

        AuthResponse resp = authService.login(req);

        assertEquals("jwt-token-456", resp.getToken());
        assertEquals("user", resp.getUsername());
    }

    @Test
    void login_shouldThrowWhenPasswordWrong() {
        LoginRequest req = new LoginRequest();
        req.setUsername("user");
        req.setPassword("wrong");

        User user = new User("user", "u@test.com", passwordEncoder.encode("correct"));

        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest req = new LoginRequest();
        req.setUsername("ghost");
        req.setPassword("any");

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(req));
    }
}
