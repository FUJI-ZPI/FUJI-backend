package com.zpi.fujibackend.auth;

public interface JwtFacade {

    String getSubject(final String token);

    boolean validateToken(final String token);

    String generateAccessToken(final String subject);

    String generateRefreshToken(final String subject);

    String getClaim(final String token, final String key);
}
