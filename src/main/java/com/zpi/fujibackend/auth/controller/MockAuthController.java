package com.zpi.fujibackend.auth.controller;

import com.zpi.fujibackend.auth.AuthFacade;
import com.zpi.fujibackend.auth.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
@Profile("!prod")
class MockAuthController {

    private final AuthFacade authFacade;

    @PostMapping("/login-mock")
    TokenDto authenticateWithGoogleMock() {
        final String googleToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJlbWFpbCI6Im15QGVtYWlsIn0.qqei_kFbDs8ALnBaOwqXIDg7n-F7sfp4FT_yXmDYLy0";
        return authFacade.generateTokenMock(googleToken);
    }
}
