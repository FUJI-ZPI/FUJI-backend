package com.zpi.fujibackend.user.controller;

import com.zpi.fujibackend.user.UserFacade;
import com.zpi.fujibackend.user.dto.FcmTokenForm;
import com.zpi.fujibackend.user.dto.FcmTokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@RestController
class UserController {

    private final UserFacade userFacade;

    @PatchMapping("/fcm-token")
    FcmTokenResponse updateFcmToken(@RequestBody @Valid FcmTokenForm tokenDto) {
        userFacade.setCurrentUserFcmToken(tokenDto.fcmToken());
        return new FcmTokenResponse(true, tokenDto.fcmToken());

    }
}
