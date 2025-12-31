package com.market.userservice.service;

import com.market.userservice.entity.RefreshToken;
import com.market.userservice.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    @Transactional
    public RefreshToken create(Long userId) {
        // Ensure only one refresh token per user
        repo.deleteByUserId(userId);

        RefreshToken token = new RefreshToken();
        token.setUserId(userId);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));

        return repo.save(token);
    }

    public RefreshToken validate(String token) {
        RefreshToken refreshToken = repo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(refreshToken);
            throw new IllegalArgumentException("Refresh token expired");
        }

        return refreshToken;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        repo.deleteByUserId(userId);
    }
}
