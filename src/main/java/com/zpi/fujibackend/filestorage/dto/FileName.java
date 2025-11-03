package com.zpi.fujibackend.filestorage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;


public record FileName(
        @NotBlank(message = "Filename must not be blank")
        @Size(min = 1, max = 255, message = "Filename length must be between 1 and 255 characters")
        @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "Filename contains invalid characters")
        String value
) {
}

