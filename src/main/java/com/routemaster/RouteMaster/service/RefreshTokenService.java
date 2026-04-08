package com.routemaster.RouteMaster.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000;

    public String createRefreshToken(Long userId) {

        String refreshToken = UUID.randomUUID().toString();
        String redisKey = "refreshToken:" + refreshToken;
        String userTokensKey = "user:" + userId + ":refreshTokens";

        // Save(Key: token, Value: userId)
        redisTemplate.opsForValue().set(redisKey, String.valueOf(userId), REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);
        
        // Add token to the user's list of active tokens
        redisTemplate.opsForSet().add(userTokensKey, refreshToken);
        redisTemplate.expire(userTokensKey, REFRESH_TOKEN_EXPIRATION, TimeUnit.MILLISECONDS);

        log.info("Refresh Token was created to Redis. UserID: {}", userId);
        return refreshToken;
    }

    public String getUserIdFromRefreshToken(String token) {
        String redisKey = "refreshToken:" + token;
        return redisTemplate.opsForValue().get(redisKey);
    }

    public void deleteRefreshToken(String token) {
        String userIdStr = getUserIdFromRefreshToken(token);
        
        if (userIdStr != null) {
            String userTokensKey = "user:" + userIdStr + ":refreshTokens";
            
            // Get all tokens active for this user
            Set<String> tokens = redisTemplate.opsForSet().members(userTokensKey);
            if (tokens != null && !tokens.isEmpty()) {
                for (String t : tokens) {
                    redisTemplate.delete("refreshToken:" + t);
                }
            }
            
            // Delete the set containing user's tokens
            redisTemplate.delete(userTokensKey);
            log.warn("All Refresh Tokens were deleted for UserID: {}", userIdStr);
        } else {
            // Delete just this token if user lookup failed
            String redisKey = "refreshToken:" + token;
            redisTemplate.delete(redisKey);
            log.warn("Fallback: Specific Refresh Token was deleted.");
        }
    }
}
