package com.zpi.fujibackend.auth.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.zpi.fujibackend.common.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
@Service
class GoogleTokenVerifierService{

    @Value("${google.clientId}")
    private String googleClientId;

    GoogleIdToken.Payload verifyToken(final String idTokenString) {
        try {
            final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            final GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new InvalidTokenException("Invalid ID token.");
            }
        } catch (Exception e) {
            throw new InvalidTokenException("Token verification failed");
        }
    }

    String verifyTokenMock(final String token) {
        try {
            final String[] parts = token.split("\\.");
            if (parts.length < 2) {
                throw new IllegalArgumentException("Invalid JWT");
            }

            final String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            final ObjectMapper objectMapper = new ObjectMapper();
            final Map<String, Object> claims = objectMapper.readValue(payloadJson, Map.class);

            return (String) claims.get("email");
        } catch (Exception e) {
            throw new InvalidTokenException("Failed to decode JWT: " + e.getMessage());
        }
    }
}
