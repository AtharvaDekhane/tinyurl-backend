package com.tinyurl.controller;

import com.tinyurl.dto.CreateAdminRequest;
import com.tinyurl.dto.LoginRequest;
import com.tinyurl.dto.LoginResponse;
import com.tinyurl.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Create admin",
            description = "Creates first admin user"
    )
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody CreateAdminRequest request) {

        return ResponseEntity.ok(authService.createAdmin(request));
    }

    @Operation(
            summary = "Login",
            description = "Authenticates user and returns JWT token"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }
}