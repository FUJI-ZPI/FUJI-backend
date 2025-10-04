package com.zpi.fujibackend.auth.domain;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.zpi.fujibackend.auth.AuthFacade;
import com.zpi.fujibackend.auth.JwtFacade;
import com.zpi.fujibackend.auth.dto.TokenDto;
import com.zpi.fujibackend.common.exception.InvalidTokenException;
import com.zpi.fujibackend.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class AuthService implements AuthFacade {

    private final JwtFacade jwtFacade;
    private final GoogleTokenVerifierService tokenVerifierService;
    private final UserFacade userFacade;

    @Override
    public TokenDto generateToken(final String googleToken) {
        final GoogleIdToken.Payload payload = tokenVerifierService.verifyToken(googleToken);
        final String subject = userFacade.findOrCreateByEmail(payload.getEmail()).toString();
        final String accessToken = jwtFacade.generateAccessToken(subject);
        final String refreshToken = jwtFacade.generateRefreshToken(subject);
        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    public TokenDto generateTokenMock(final String googleToken) {
        final String email = tokenVerifierService.verifyTokenMock(googleToken);
        final String subject = userFacade.findOrCreateByEmail(email).toString();
        final String accessToken = jwtFacade.generateAccessToken(subject);
        final String refreshToken = jwtFacade.generateRefreshToken(subject);
        return new TokenDto(accessToken, refreshToken);
    }

    @Override
    public TokenDto getRefreshToken(final String refreshToken) {
        if (!jwtFacade.validateToken(refreshToken)) {
            throw new InvalidTokenException("Invalid token");
        }

        final String subject = jwtFacade.getSubject(refreshToken);
        final String type = jwtFacade.getClaim(refreshToken, "type");

        if (!"refresh".equals(type)) {
            throw new InvalidTokenException("Not a refresh token");
        }

        return generateToken(subject);
    }
}
