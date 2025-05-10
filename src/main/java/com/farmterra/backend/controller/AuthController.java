package com.farmterra.backend.controller;

import com.farmterra.backend.dto.LoginRequest;
import com.farmterra.backend.dto.RegisterRequest;
import com.farmterra.backend.dto.UserDto;
import com.farmterra.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDto registeredUser = authService.register(registerRequest);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        UserDto user = authService.getUserByEmail(loginRequest.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
}
