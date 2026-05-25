package com.tinyurl.controller;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.service.RateLimiterService;
import com.tinyurl.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    private final RateLimiterService rateLimiterService;

    @PostMapping("/api/v1/url")
    public ResponseEntity<CreateUrlResponse> createShortUrl(
            @Valid @RequestBody CreateUrlRequest request) {

        return ResponseEntity.ok(
                urlService.createShortUrl(request)
        );
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request
    ) {

        String ipAddress =
                request.getRemoteAddr();

        rateLimiterService
                .validateRateLimit(ipAddress);

        String originalUrl =
                urlService.getOriginalUrl(shortCode);

        HttpHeaders headers = new HttpHeaders();

        headers.setLocation(
                URI.create(originalUrl)
        );

        return ResponseEntity
                .status(302)
                .headers(headers)
                .build();
    }
}