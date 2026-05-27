package com.tinyurl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/dashboard")
    public ResponseEntity<String> userDashboard() {

        return ResponseEntity.ok("Welcome User");
    }
}