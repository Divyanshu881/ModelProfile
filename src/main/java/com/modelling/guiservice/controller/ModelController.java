package com.modelling.guiservice.controller;

import com.modelling.guiservice.dto.helper.HelperPage;
import com.modelling.guiservice.dto.request.ModelRequest;
import com.modelling.guiservice.dto.request.ViewRequest;
import com.modelling.guiservice.dto.response.ModelResponse;
import com.modelling.guiservice.service.ModelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
@Slf4j
public class ModelController {
    private final ModelService modelService;

    @PostMapping("/create")
    public ResponseEntity<ModelResponse> createModel(@Valid @ModelAttribute ModelRequest request) {
        log.info("Received request to create new model");
        ModelResponse response = modelService.createModel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelResponse> getModelById(@PathVariable String id) {
        log.info("Received request to get model with ID: {}", id);
        ModelResponse response = modelService.getModelById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/view")
    public ResponseEntity<HelperPage<ModelResponse>> searchModels(@Valid @RequestBody ViewRequest viewRequest) throws Exception {

        log.info("Received search request with parameters {}", viewRequest);

        HelperPage<ModelResponse> response = modelService.searchModels(viewRequest);
        response.setMessage("Models retrieved successfully");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<ModelResponse> updateModel(
            @PathVariable String id,
            @Valid @RequestBody ModelRequest request) {

        log.info("Received request to update model with ID: {}", id);
        ModelResponse response = modelService.updateModel(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable String id) {
        log.info("Received request to delete model with ID: {}", id);
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }
}
