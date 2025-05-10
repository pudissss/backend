package com.farmterra.backend.service;

import com.farmterra.backend.dto.LoginRequest;
import com.farmterra.backend.dto.RegisterRequest;
import com.farmterra.backend.dto.UserDto;
import com.farmterra.backend.model.User;
import com.farmterra.backend.repository.UserRepository;
import com.farmterra.backend.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    public UserDto register(RegisterRequest registerRequest) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Create new user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());
        user.setAvatar(registerRequest.getAvatar());
        
        // Farmer-specific fields
        if (registerRequest.getRole() == User.UserRole.FARMER) {
            user.setFarmName(registerRequest.getFarmName());
            user.setFarmDescription(registerRequest.getFarmDescription());
        }

        // Save user
        User savedUser = userRepository.save(user);

        // Convert to DTO
        UserDto userDto = new UserDto();
        userDto.setId(savedUser.getId());
        userDto.setName(savedUser.getName());
        userDto.setEmail(savedUser.getEmail());
        userDto.setRole(savedUser.getRole());
        userDto.setPhone(savedUser.getPhone());
        userDto.setAddress(savedUser.getAddress());
        userDto.setAvatar(savedUser.getAvatar());
        userDto.setFarmName(savedUser.getFarmName());
        userDto.setFarmDescription(savedUser.getFarmDescription());

        return userDto;
    }

    public String login(LoginRequest loginRequest) {
        logger.info("Login attempt for email: '{}' (length: {})", loginRequest.getEmail(), loginRequest.getEmail().length());
        userRepository.findAll().forEach(u -> {
            String dbEmail = u.getEmail();
            logger.info("Existing user email: '{}' (length: {}) [hex: {}]", dbEmail, dbEmail.length(), toHex(dbEmail));
        });
        logger.info("Login email hex: {}", toHex(loginRequest.getEmail()));
        User user = userRepository.findByEmailIgnoreCase(loginRequest.getEmail()).orElse(null);
        if (user == null) {
            logger.warn("User not found for email: '{}'", loginRequest.getEmail());
        } else {
            logger.info("User found: {} (id: {})", user.getEmail(), user.getId());
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(), 
                    loginRequest.getPassword()
                )
            );
            logger.info("Authentication successful for email: {}", loginRequest.getEmail());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtTokenUtil.generateToken(userDetails);
        } catch (Exception ex) {
            logger.error("Authentication failed for email: {}: {}", loginRequest.getEmail(), ex.getMessage());
            throw ex;
        }
    }

    private String toHex(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append(String.format("%02x ", (int) c));
        }
        return sb.toString().trim();
    }

    public UserDto getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User user = userRepository.findByEmailIgnoreCase(currentUsername)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        userDto.setAvatar(user.getAvatar());
        userDto.setFarmName(user.getFarmName());
        userDto.setFarmDescription(user.getFarmDescription());
        return userDto;
    }

    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setPhone(user.getPhone());
        userDto.setAddress(user.getAddress());
        userDto.setAvatar(user.getAvatar());
        userDto.setFarmName(user.getFarmName());
        userDto.setFarmDescription(user.getFarmDescription());
        return userDto;
    }
}
