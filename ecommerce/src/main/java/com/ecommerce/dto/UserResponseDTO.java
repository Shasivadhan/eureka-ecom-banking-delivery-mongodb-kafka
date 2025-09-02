package com.ecommerce.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    // Add more non-sensitive fields if you want, but NO password!
}
