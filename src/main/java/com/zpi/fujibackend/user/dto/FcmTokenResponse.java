package com.zpi.fujibackend.user.dto;


public record FcmTokenResponse(
        boolean success,
        String fcmToken
) {
}

