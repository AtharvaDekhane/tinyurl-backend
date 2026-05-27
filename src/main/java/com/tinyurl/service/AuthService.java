package com.tinyurl.service;

import com.tinyurl.dto.CreateAdminRequest;
import com.tinyurl.dto.LoginRequest;
import com.tinyurl.dto.LoginResponse;
import com.tinyurl.enums.Role;
import com.tinyurl.entity.User;
import com.tinyurl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String createAdmin(CreateAdminRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setUrlQuota(1000);
        user.setFirstLogin(false);
        user.setIsActive(true);

        userRepository.save(user);
        return "Admin created successfully";
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail()
        ).orElseThrow(() ->
                new RuntimeException("Invalid email or password")
        );

        boolean passwordMatches =
                passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token);
    }
}