package com.ecommerce.service.impl;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private UserServiceImpl userService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    // ✅ Mongo-style ID
    private final String USER_ID = "507f1f77bcf86cd799439051";

    @BeforeEach
    public void setup() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Alice");
        registerRequest.setEmail("alice@example.com");
        registerRequest.setPassword("123456");
        registerRequest.setPhone("9876543210");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("alice@example.com");
        loginRequest.setPassword("123456");

        mockUser = new User();
        mockUser.setId(USER_ID); // ✅ String id
        mockUser.setFullName("Alice");
        mockUser.setEmail("alice@example.com");
        mockUser.setPassword("123456");
        mockUser.setPhone("9876543210");
    }

    @Test
    public void testRegisterUser_Success() {
        when(userRepo.existsByEmail("alice@example.com")).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(mockUser);

        UserResponseDTO result = userService.registerUser(registerRequest);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId()); // ✅ String
        assertEquals("Alice", result.getFullName());
        assertEquals("alice@example.com", result.getEmail());
        assertEquals("9876543210", result.getPhone());

        verify(userRepo).existsByEmail("alice@example.com");
        verify(userRepo).save(any(User.class));
    }

    @Test
    public void testRegisterUser_AlreadyExists() {
        when(userRepo.existsByEmail("alice@example.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.registerUser(registerRequest));

        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
        assertEquals("Email already registered", ex.getReason());

        verify(userRepo).existsByEmail("alice@example.com");
        verify(userRepo, never()).save(any(User.class));
    }

    @Test
    public void testLogin_Success() {
        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.of(mockUser));

        UserResponseDTO result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals("alice@example.com", result.getEmail());
        assertEquals("Alice", result.getFullName());
        assertEquals("9876543210", result.getPhone());

        verify(userRepo).findByEmail("alice@example.com");
    }

    @Test
    public void testLogin_InvalidEmail() {
        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.login(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Invalid email or password", ex.getReason());

        verify(userRepo).findByEmail("alice@example.com");
    }

    @Test
    public void testLogin_InvalidPassword() {
        mockUser.setPassword("wrongpassword");
        when(userRepo.findByEmail("alice@example.com")).thenReturn(Optional.of(mockUser));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> userService.login(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        assertEquals("Invalid email or password", ex.getReason());

        verify(userRepo).findByEmail("alice@example.com");
    }
}
