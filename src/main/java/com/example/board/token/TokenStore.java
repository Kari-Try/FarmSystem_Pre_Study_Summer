// token/TokenStore.java
package com.example.board.token;

import com.example.board.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenStore {
    private final StringRedisTemplate redis;
    private final JwtProvider jwt;

    public void saveRefresh(Long userId, String refresh, long ttlSeconds) {
        redis.opsForValue().set("RT:" + userId, refresh, Duration.ofSeconds(ttlSeconds));
    }

    public boolean matchRefresh(Long userId, String refresh) {
        String saved = redis.opsForValue().get("RT:" + userId);
        return refresh.equals(saved);
    }

    public void revokeRefresh(Long userId) {
        redis.delete("RT:" + userId);
    }

    public void blacklistAccess(String accessToken) {
        long remain = jwt.getRemainingSeconds(accessToken);
        if (remain > 0) {
            redis.opsForValue().set("BL:" + accessToken, "1", Duration.ofSeconds(remain));
        }
    }
}
