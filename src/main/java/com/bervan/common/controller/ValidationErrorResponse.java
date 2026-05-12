package com.bervan.common.controller;

import com.bervan.common.config.EntityConfigValidator;

import java.util.List;

/**
 * Wrapper for validation errors returned by controllers.
 */
public class ValidationErrorResponse {
    private final List<EntityConfigValidator.FieldError> errors;

    public ValidationErrorResponse(List<EntityConfigValidator.FieldError> errors) {
        this.errors = errors;
    }

    public List<EntityConfigValidator.FieldError> getErrors() {
        return errors;
    }
}

