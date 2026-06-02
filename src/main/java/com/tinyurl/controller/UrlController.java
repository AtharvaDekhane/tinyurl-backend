package com.tinyurl.controller;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.dto.UrlListResponse;
import com.tinyurl.service.RateLimiterService;
import com.tinyurl.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final RateLimiterService rateLimiterService;

    @Operation(summary = "Create short URL", description = "Creates a short URL for a given original URL")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/api/v1/url")
    public ResponseEntity<CreateUrlResponse> createShortUrl(@Valid @RequestBody CreateUrlRequest request) {

        return ResponseEntity.ok(urlService.createShortUrl(request));
    }

    @Operation(summary = "Redirect short URL", description = "Redirects short URL to original URL")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        rateLimiterService.validateRateLimit(ipAddress);
        String originalUrl = urlService.getOriginalUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(originalUrl));

        return ResponseEntity.status(302).headers(headers).build();
    }

    @Operation(summary = "Delete URL", description = "Deletes URL by ID")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUrl(@PathVariable Long id) {

        urlService.deleteUrl(id);
        return ResponseEntity.ok("URL deleted successfully");
    }

    @Operation(summary = "Get all URLs", description = "Returns all stored URLs")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UrlListResponse>> getAllUrls() {

        return ResponseEntity.ok(urlService.getAllUrls());
    }
}