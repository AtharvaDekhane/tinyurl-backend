package com.tinyurl.controller;

import com.tinyurl.dto.AddUserRequest;
import com.tinyurl.dto.UpdateQuotaRequest;
import com.tinyurl.dto.UserResponse;
import com.tinyurl.service.AuthService;
import com.tinyurl.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MailService mailService;
    private final AuthService authService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<String> adminDashboard() {

        return ResponseEntity.ok("Welcome Admin");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test-email")
    public ResponseEntity<String> testEmail() {

        mailService.sendEmail(
                "atharvadekhane5@gmail.com",
                "TinyURL Test Mail",
                "Email integration working successfully"
        );

        return ResponseEntity.ok("Email sent successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/users")
    public ResponseEntity<String> addUser(@RequestBody AddUserRequest request) {
        return ResponseEntity.ok(authService.addUser(request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable Long id) {
        return ResponseEntity.ok(authService.deactivateUser(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/quota")
    public ResponseEntity<String> updateQuota(@PathVariable Long id, @RequestBody UpdateQuotaRequest request) {
        return ResponseEntity.ok(authService.updateQuota(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/quota-zero")
    public ResponseEntity<String> makeQuotaZero(@PathVariable Long id) {
        return ResponseEntity.ok(authService.makeQuotaZero(id));
    }
}