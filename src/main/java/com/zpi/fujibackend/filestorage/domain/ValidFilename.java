package com.zpi.fujibackend.filestorage.domain;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FilenameValidator.class)
public @interface ValidFilename {

    String message() default "Invalid filename";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}


