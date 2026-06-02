package com.tinyurl.service;

import com.tinyurl.exception.RateLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_REQUESTS = 100;
    private static final Duration WINDOW = Duration.ofMinutes(1);

    public void validateRateLimit(String ipAddress) {
        String key = "rate_limit:" + ipAddress;
        String currentCount = redisTemplate.opsForValue().get(key);

        // First request
        if (currentCount == null) {
            redisTemplate.opsForValue().set(key, "1", WINDOW);
            return;
        }

        int requests = Integer.parseInt(currentCount);
        if (requests >= MAX_REQUESTS) {
            throw new RateLimitExceededException("Too many requests");
        }

        redisTemplate.opsForValue().increment(key);
    }
}