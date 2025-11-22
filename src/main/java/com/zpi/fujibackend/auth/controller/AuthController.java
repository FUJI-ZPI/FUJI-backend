package com.zpi.fujibackend.auth.controller;

import com.zpi.fujibackend.auth.AuthFacade;
import com.zpi.fujibackend.auth.dto.TokenDto;
import com.zpi.fujibackend.auth.dto.TokenVerificationForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private static final class Routes {
        private static final String LOGIN = "/login";
        private static final String REFRESH = "/refresh";
    }

    private final AuthFacade authFacade;

    @PostMapping(Routes.LOGIN)
    TokenDto authenticateWithGoogle(@RequestBody @Valid final TokenVerificationForm form) {
        return authFacade.generateToken(form.token());
    }

    @PostMapping(Routes.REFRESH)
    TokenDto refresh(@RequestBody @Valid final TokenVerificationForm form) {
        return authFacade.getRefreshToken(form.token());
    }
}
