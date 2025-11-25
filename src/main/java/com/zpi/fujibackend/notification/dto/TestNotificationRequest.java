package com.zpi.fujibackend.notification.dto;

import jakarta.validation.constraints.NotBlank;

public record TestNotificationRequest(
        @NotBlank
        String title,
        @NotBlank
        String body

) {
}
