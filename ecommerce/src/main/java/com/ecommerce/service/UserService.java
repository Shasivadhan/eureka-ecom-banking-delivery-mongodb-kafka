package com.ecommerce.service;

import com.ecommerce.dto.LoginRequest;
import com.ecommerce.dto.RegisterRequest;
import com.ecommerce.dto.UserResponseDTO;
import com.ecommerce.entity.User;

public interface UserService {
    UserResponseDTO registerUser(RegisterRequest request);
    UserResponseDTO login(LoginRequest request);
    User getUserById(String userId);   // <-- changed from Long to String
}
