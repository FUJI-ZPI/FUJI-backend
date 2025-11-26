package com.zpi.fujibackend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record FcmTokenForm(
        @NotBlank
        String fcmToken
) {
}
