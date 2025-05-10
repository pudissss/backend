package com.farmterra.backend.dto;

import com.farmterra.backend.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private User.UserRole role;
    private String phone;
    private String address;
    private String avatar;
    private String farmName;
    private String farmDescription;
}
