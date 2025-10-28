package com.zpi.fujibackend.common.exception;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record ApiErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        String error
) {
    public static ApiErrorResponse now(String error) {
        return new ApiErrorResponse(LocalDateTime.now(), error);
    }
}
