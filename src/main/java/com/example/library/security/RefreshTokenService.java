package com.example.library.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "refresh:";

    public void save(String username, String refreshToken, long ttlMillis) {
        redisTemplate.opsForValue()
                .set(PREFIX + username, refreshToken,
                        Duration.ofMillis(ttlMillis));
    }

    public String get(String username) {
        return redisTemplate.opsForValue().get(PREFIX + username);
    }

    public void delete(String username) {
        redisTemplate.delete(PREFIX + username);
    }
}