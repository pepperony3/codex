package com.taskboard.controller;

import com.taskboard.config.JwtUtil;
import com.taskboard.config.SecurityConfig;
import com.taskboard.dto.AuthResponse;
import com.taskboard.dto.RegisterRequest;
import com.taskboard.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void register_shouldReturnToken() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(new AuthResponse("jwt-abc", "testuser"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"testuser","email":"test@test.com","password":"password123"}"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-abc"))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void register_shouldRejectShortUsername() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"ab","email":"test@test.com","password":"password123"}"""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_shouldRejectInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"validuser","email":"notanemail","password":"password123"}"""))
                .andExpect(status().isBadRequest());
    }
}
