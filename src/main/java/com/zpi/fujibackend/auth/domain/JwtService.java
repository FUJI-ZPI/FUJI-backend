package com.zpi.fujibackend.auth.domain;

import com.zpi.fujibackend.auth.JwtFacade;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@RequiredArgsConstructor
@Service
class JwtService implements JwtFacade {

    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 days
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 30; // 30 days

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public boolean validateToken(final String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (final JwtException e) {
            return false;
        }
    }

    @Override
    public String getSubject(final String token) {
        return getAllClaims(token).get("sub", String.class);
    }

    @Override
    public String generateAccessToken(final String subject) {
        return Jwts.builder()
                .claims()
                .add("sub", subject)
                .add("type", "access")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .claims()
                .add("sub", subject)
                .add("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .and()
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String getClaim(final String token, final String key) {
        return getAllClaims(token).get(key, String.class);
    }

    private Claims getAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        final byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
