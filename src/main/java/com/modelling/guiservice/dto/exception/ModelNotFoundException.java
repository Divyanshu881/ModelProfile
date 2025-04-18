package com.modelling.guiservice.dto.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ModelNotFoundException extends RuntimeException {
    private final String modelId;

    public ModelNotFoundException(String modelId) {
        super(String.format("Model not found with ID: %s", modelId));
        this.modelId = modelId;
    }
}