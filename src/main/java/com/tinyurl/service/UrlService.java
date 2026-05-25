package com.tinyurl.service;

import com.tinyurl.dto.CreateUrlRequest;
import com.tinyurl.dto.CreateUrlResponse;
import com.tinyurl.dto.UrlClickEvent;
import com.tinyurl.entity.Url;
import com.tinyurl.repository.UrlRepository;
import com.tinyurl.util.Base62Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository repository;
    private final RedisTemplate<String, String> redisTemplate;
    private final KafkaProducerService kafkaProducerService;

    public CreateUrlResponse createShortUrl(CreateUrlRequest request) {

        Url url = Url.builder()
                .originalUrl(request.getOriginalUrl())
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        url = repository.save(url);

        String shortCode = Base62Util.encode(url.getId());

        url.setShortCode(shortCode);

        repository.save(url);

        return CreateUrlResponse.builder()
                .shortUrl("http://localhost:8080/" + shortCode)
                .build();
    }

    public String getOriginalUrl(String shortCode) {

        String cacheKey = "url:" + shortCode;

        String cachedUrl =
                redisTemplate.opsForValue().get(cacheKey);

        if (cachedUrl != null) {

            kafkaProducerService.publishUrlClickEvent(
                    UrlClickEvent.builder()
                            .shortCode(shortCode)
                            .build()
            );

            return cachedUrl;
        }

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));

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
}