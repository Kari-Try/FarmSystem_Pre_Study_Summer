package com.example.board.token;

import com.example.board.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenStore {
    private final StringRedisTemplate redis;
    private final JwtProvider jwt;

    private void safeRun(Runnable r) {
        try {
            r.run();
        } catch (RuntimeException e) { // DataAccessException 포함
            log.warn("[TokenStore] Redis unavailable. Skip op. cause={}", e.toString());
        }
    }

    private <T> T safeGet(Supplier<T> s, T fallback) {
        try {
            return s.get();
        } catch (RuntimeException e) { // DataAccessException 포함
            log.warn("[TokenStore] Redis unavailable. Return fallback={}. cause={}", fallback, e.toString());
            return fallback;
        }
    }

    public void saveRefresh(Long userId, String refresh, long ttlSeconds) {
        safeRun(() -> redis.opsForValue().set("RT:" + userId, refresh, Duration.ofSeconds(ttlSeconds)));
    }

    public boolean matchRefresh(Long userId, String refresh) {
        return safeGet(() -> refresh.equals(redis.opsForValue().get("RT:" + userId)), false);
    }

    public void revokeRefresh(Long userId) {
        safeRun(() -> redis.delete("RT:" + userId));
    }

    public void blacklistAccess(String accessToken) {
        long remain = jwt.getRemainingSeconds(accessToken);
        if (remain > 0) {
            safeRun(() -> redis.opsForValue().set("BL:" + accessToken, "1", Duration.ofSeconds(remain)));
        }
    }

    public boolean isBlacklisted(String accessToken) {
        return safeGet(() -> Boolean.TRUE.equals(redis.hasKey("BL:" + accessToken)), false);
    }
}
