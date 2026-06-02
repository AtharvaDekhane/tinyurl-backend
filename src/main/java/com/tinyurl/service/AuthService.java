package com.tinyurl.service;

import com.tinyurl.dto.*;
import com.tinyurl.enums.Role;
import com.tinyurl.entity.User;
import com.tinyurl.exception.*;
import com.tinyurl.util.PasswordUtil;
import com.tinyurl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    public String createAdmin(CreateAdminRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
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

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new InvalidEmailException("Invalid email."));

        boolean passwordMatches =
                passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            throw new InvalidPasswordException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
        return new LoginResponse(token);
    }

    public String addUser(AddUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        String tempPassword = PasswordUtil.generatePassword();

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(tempPassword));
        user.setRole(request.getRole());
        user.setUrlQuota(request.getUrlQuota());
        user.setFirstLogin(true);
        user.setIsActive(true);
        user.setPasswordExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        String emailBody =
                "Welcome to TinyURL Platform\n\n"
                        + "Email: "
                        + user.getEmail()
                        + "\n\nTemporary Password: "
                        + tempPassword
                        + "\n\nPlease change your password within 24 hours.";

        mailService.sendEmail(
                user.getEmail(),
                "TinyURL User Onboarding",
                emailBody
        );

        return "User created successfully";
    }

    public String changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getFirstLogin()) {
            throw new PasswordAlreadyChangedException("Password already changed");
        }

        if (user.getPasswordExpiry() != null && LocalDateTime.now().isAfter(user.getPasswordExpiry())) {
            throw new TemporaryPasswordExpiredException("Temporary password expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setFirstLogin(false);
        user.setPasswordExpiry(null);

        userRepository.save(user);
        return "Password changed successfully";
    }

    public String resendPasswordLink(ResendPasswordLinkRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        String emailBody =
                "Reset your password using the link below:\n\n"
                        + resetLink
                        + "\n\nLink valid for 24 hours.";

        mailService.sendEmail(
                user.getEmail(),
                "TinyURL Password Reset",
                emailBody
        );

        return "Password reset link sent";
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll().stream().map(user ->
                        new UserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole(),
                                user.getUrlQuota(),
                                user.getIsActive()
                        ))
                .toList();
    }

    public String deactivateUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setIsActive(false);
        userRepository.save(user);
        return "User deactivated successfully";
    }

    public String updateQuota(Long userId, UpdateQuotaRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Integer userQuota = user.getUrlQuota() + request.getUrlQuota();
        user.setUrlQuota(userQuota);
        userRepository.save(user);

        return "Quota updated successfully";
    }

    public MeResponse getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new MeResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getUrlQuota(),
                user.getIsActive()
        );
    }

    public String makeQuotaZero(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setUrlQuota(0);
        userRepository.save(user);

        return "User quota set to zero";
    }
}