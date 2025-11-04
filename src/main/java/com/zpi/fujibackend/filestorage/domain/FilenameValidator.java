package com.zpi.fujibackend.filestorage.domain;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilenameValidator implements ConstraintValidator<ValidFilename, String> {

    private static final String FILENAME_PATTERN = "^(?!.*\\.\\.)([A-Za-z0-9._-]+)$";
    private static final int MAX_LENGTH = 255;

    @Override
    public boolean isValid(String filename, ConstraintValidatorContext context) {
        log.info("Validating filename: {}", filename);
        if (filename == null || filename.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Filename must not be blank")
                    .addConstraintViolation();
            return false;
        }

        if (filename.length() > MAX_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            String.format("Filename length must not exceed %d characters", MAX_LENGTH))
                    .addConstraintViolation();
            return false;
        }

        if (!filename.matches(FILENAME_PATTERN)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                            "Filename contains invalid characters. Only letters, numbers, dots, hyphens and underscores are allowed")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}

