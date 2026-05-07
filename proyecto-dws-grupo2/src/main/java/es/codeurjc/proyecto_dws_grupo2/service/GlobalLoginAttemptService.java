package es.codeurjc.proyecto_dws_grupo2.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class GlobalLoginAttemptService {

    private static final Logger log = LoggerFactory.getLogger(GlobalLoginAttemptService.class);

    private final int maxAttempts;
    private final int blockDurationMinutes;

    // FIX: Caffeine cache with TTL prevents memory leak from entries never cleaned up
    private final Cache<String, LoginAttemptInfo> attemptsCache;

    public GlobalLoginAttemptService(
            @Value("${security.login.max-attempts:5}") int maxAttempts,
            @Value("${security.login.block-duration-minutes:15}") int blockDurationMinutes) {
        this.maxAttempts = maxAttempts;
        this.blockDurationMinutes = blockDurationMinutes;
        this.attemptsCache = Caffeine.newBuilder()
                .expireAfterWrite(blockDurationMinutes + 5, TimeUnit.MINUTES)
                .maximumSize(10_000)
                .build();
    }

    private static class LoginAttemptInfo {

        // FIX: AtomicInteger + synchronized methods prevent race conditions
        // under concurrent login attempts for the same email
        private final AtomicInteger attempts = new AtomicInteger(0);
        private volatile LocalDateTime blockedUntil = null;

        public synchronized void increment(int maxAttempts, int blockDurationMinutes) {
            int current = attempts.incrementAndGet();
            if (current >= maxAttempts && blockedUntil == null) {
                blockedUntil = LocalDateTime.now().plusMinutes(blockDurationMinutes);
            }
        }

        // FIX: isBlocked() is now a pure read — no hidden side effects
        public synchronized boolean isBlocked() {
            if (blockedUntil == null) return false;
            return LocalDateTime.now().isBefore(blockedUntil);
        }

        // FIX: Expiry check separated from block check — caller decides what to do
        public synchronized boolean hasExpired() {
            return blockedUntil != null && LocalDateTime.now().isAfter(blockedUntil);
        }

        public synchronized void reset() {
            attempts.set(0);
            blockedUntil = null;
        }

        public int getAttempts() {
            return attempts.get();
        }
    }

    @EventListener
    public void onFailedLogin(AuthenticationFailureBadCredentialsEvent event) {
        String email = event.getAuthentication().getName();
        LoginAttemptInfo info = attemptsCache.get(email, k -> new LoginAttemptInfo());
        info.increment(maxAttempts, blockDurationMinutes);
        log.warn("Failed login attempt for: {}. Total failures: {}", email, info.getAttempts());
    }

    @EventListener
    public void onSuccessLogin(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        attemptsCache.invalidate(email);
        log.info("Successful login for: {}. Counter reset.", email);
    }

    public boolean isBlocked(String email) {
        LoginAttemptInfo info = attemptsCache.getIfPresent(email);
        if (info == null) return false;

        // FIX: If the block period has naturally expired, evict it now
        if (info.hasExpired()) {
            attemptsCache.invalidate(email);
            return false;
        }

        return info.isBlocked();
    }
}