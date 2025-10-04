package com.zpi.fujibackend.auth.dto;

import jakarta.validation.constraints.NotNull;

public record TokenVerificationForm(@NotNull String token) {
}
