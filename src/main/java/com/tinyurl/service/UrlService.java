package com.tinyurl.service;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.dto.UrlClickEvent;
import com.tinyurl.dto.UrlListResponse;
import com.tinyurl.entity.Url;
import com.tinyurl.exception.QuotaExceededException;
import com.tinyurl.exception.UnauthorizedException;
import com.tinyurl.exception.UrlNotFoundException;
import com.tinyurl.exception.UserNotFoundException;
import com.tinyurl.repository.UrlRepository;
import com.tinyurl.entity.User;
import com.tinyurl.repository.UserRepository;
import com.tinyurl.util.Base62Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaProducerService kafkaProducerService;
    private final UserRepository userRepository;

    public CreateUrlResponse createShortUrl(CreateUrlRequest request) {
        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        long userUrlCount = repository.countByUser(user);

        if (userUrlCount >= user.getUrlQuota()) {
            throw new QuotaExceededException("URL quota exceeded");
        }

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        url = repository.save(url);
        String shortCode = Base62Util.encode(url.getId());
        url.setShortCode(shortCode);
        url.setUser(user);

        repository.save(url);
        return CreateUrlResponse.builder().shortUrl("http://localhost:8080/" + shortCode).build();
    }

    public String getOriginalUrl(String shortCode) {

        String cacheKey = "url:" + shortCode;
        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);

        if (cachedUrl != null) {
            kafkaProducerService.publishUrlClickEvent(
                    UrlClickEvent.builder()
                            .shortCode(shortCode)
                            .build()
            );
            return cachedUrl;
        }

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("URL not found"));

        redisTemplate.opsForValue().set(
                cacheKey,
                url.getOriginalUrl(),
                Duration.ofHours(24)
        );

        kafkaProducerService.publishUrlClickEvent(
                UrlClickEvent.builder()
                        .shortCode(shortCode)
                        .build()
        );

        return url.getOriginalUrl();
    }

    public void deleteUrl(Long id) {
        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

        Url url = repository.findById(id).orElseThrow(() ->
                new UrlNotFoundException("URL not found"));

        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isOwner = url.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new UnauthorizedException("Access denied");
        }

        redisTemplate.delete("url:" + url.getShortCode());
        repository.delete(url);
    }

    public List<UrlListResponse> getAllUrls() {
        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Url> urls;
        if (user.getRole().name().equals("ADMIN")) {
            urls = repository.findAll();
        }
        else {
            urls = repository.findByUser(user);
        }

        return urls.stream()
                .map(url -> new UrlListResponse(url.getId(), url.getOriginalUrl()))
                .toList();
    }
}