package com.ecommerce.service.impl;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepo;
import com.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepo userRepo;

    // Optional: plain-text fallback if no encoder bean is configured
    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDTO registerUser(RegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        // Build entity manually
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder != null
                ? passwordEncoder.encode(request.getPassword())
                : request.getPassword());
        user.setPhone(request.getPhone());

        user = userRepo.save(user);
        return toDto(user);
    }

    @Override
    public UserResponseDTO login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

        boolean matches = (passwordEncoder != null)
                ? passwordEncoder.matches(request.getPassword(), user.getPassword())
                : request.getPassword().equals(user.getPassword());

        if (!matches) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        return toDto(user);
    }

    @Override
    public User getUserById(String userId) {  // changed Long -> String
        return userRepo.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));
    }

    private UserResponseDTO toDto(User u) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(u.getId());   // Mongo _id is String
        dto.setFullName(u.getFullName());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        return dto;
    }
}
