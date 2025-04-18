package com.modelling.guiservice.service.impl;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.modelling.guiservice.dto.exception.ModelNotFoundException;
import com.modelling.guiservice.dto.helper.ElasticSearchResult;
import com.modelling.guiservice.dto.helper.HelperPage;
import com.modelling.guiservice.dto.request.ModelRequest;
import com.modelling.guiservice.dto.request.ViewRequest;
import com.modelling.guiservice.dto.response.ModelResponse;
import com.modelling.guiservice.model.ModelImage;
import com.modelling.guiservice.model.ModelProfile;
import com.modelling.guiservice.repository.ModelRepository;
import com.modelling.guiservice.service.FileStorageService;
import com.modelling.guiservice.service.ModelService;
import com.modelling.guiservice.utility.ElasticSearchUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class ModelServiceImpl implements ModelService {
    private static final String indexName = "model_profiles";
    private final ModelRepository modelRepository;
    private final FileStorageService fileStorageService;
    private final ElasticSearchUtility elasticSearchUtility;

    @Transactional
    public ModelResponse createModel(ModelRequest request) {
        log.info("Creating new model with name: {}", request.getName());

        try {
            // Upload images
            List<String> imageUrls = fileStorageService.uploadFiles(request.getImages());

            // Create model images
            List<ModelImage> modelImages = imageUrls.stream()
                    .map(url -> ModelImage.builder()
                            .url(url)
                            .type("PORTFOLIO")
                            .order(0)
                            .build())
                    .collect(Collectors.toList());

            // Build model
            ModelProfile model = ModelProfile.builder()
                    .name(request.getName())
                    .age(request.getAge())
                    .height(request.getHeight())
                    .gender(request.getGender())
                    .bust(request.getBust())
                    .chest(request.getChest())
                    .waist(request.getWaist())
                    .lowerWaist(request.getLowerWaist())
                    .hips(request.getHips())
                    .shoeSize(request.getShoeSize())
                    .eyes(request.getEyes())
                    .isBooked(false)
                    .images(modelImages)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            // Save to Elasticsearch
            ModelProfile savedModel = modelRepository.save(model);
            log.info("Successfully created model with ID: {}", savedModel.getId());

            return mapToResponse(savedModel);
        } catch (Exception e) {
            log.error("Error creating model: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create model", e);
        }
    }

    public ModelResponse getModelById(String id) {
        log.info("Fetching model with ID: {}", id);
        ModelProfile model = modelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Model not found with ID: {}", id);
                    return new ModelNotFoundException(id);
                });
        return mapToResponse(model);
    }

    public HelperPage<ModelResponse> searchModels(ViewRequest request) throws Exception {
        log.info("Request received to fetch data from es");

        String sortField = StringUtils.defaultIfBlank(request.getSortValue(), "id");
        Sort.Direction sortDir = Sort.Direction.fromOptionalString(request.getSortOrder()).orElse(Sort.Direction.DESC);
        Pageable pageable = PageRequest.of(request.getPage(), request.getPageSize(), sortDir, sortField);

        try {
            ElasticSearchResult<ModelProfile> searchResult = elasticSearchUtility.search(indexName, pageable, ModelProfile.class, request.getFieldSearchMap(), request.getGlobalSearch());
            log.info("Total {} entries fetched from es", searchResult.getTotalCount());
            return new HelperPage<>(!searchResult.getResults().isEmpty() ? searchResult.getResults().stream().map(this::mapToResponse).collect(Collectors.toList()) : Collections.emptyList(), pageable, searchResult.getTotalCount());
        } catch (Exception e) {
            log.error("Failed to extract data from es {}", e.getMessage(), e);
            throw new Exception(e);
        }
    }

    @Transactional
    public ModelResponse updateModel(String id, ModelRequest request) {
        log.info("Updating model with ID: {}", id);

        ModelProfile existingModel = modelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Model not found with ID: {}", id);
                    return new ModelNotFoundException(id);
                });

        // Update fields
        existingModel.setName(request.getName());
        existingModel.setAge(request.getAge());
        existingModel.setHeight(request.getHeight());
        existingModel.setGender(request.getGender());
        existingModel.setBust(request.getBust());
        existingModel.setChest(request.getChest());
        existingModel.setWaist(request.getWaist());
        existingModel.setLowerWaist(request.getLowerWaist());
        existingModel.setHips(request.getHips());
        existingModel.setShoeSize(request.getShoeSize());
        existingModel.setEyes(request.getEyes());
        existingModel.setUpdatedAt(LocalDateTime.now());

        ModelProfile updatedModel = modelRepository.save(existingModel);
        log.info("Successfully updated model with ID: {}", id);

        return mapToResponse(updatedModel);
    }

    @Transactional
    public void deleteModel(String id) {
        log.info("Deleting model with ID: {}", id);

        ModelProfile model = modelRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Model not found with ID: {}", id);
                    return new ModelNotFoundException(id);
                });

        try {
            // Delete associated images
            for (ModelImage image : model.getImages()) {
                fileStorageService.deleteFile(image.getUrl());
            }

            // Delete from Elasticsearch
            modelRepository.delete(model);
            log.info("Successfully deleted model with ID: {}", id);
        } catch (Exception e) {
            log.error("Error deleting model: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete model", e);
        }
    }

    private ModelResponse mapToResponse(ModelProfile model) {
        return ModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .age(model.getAge())
                .height(model.getHeight())
                .gender(model.getGender())
                .bust(model.getBust())
                .chest(model.getChest())
                .waist(model.getWaist())
                .lowerWaist(model.getLowerWaist())
                .hips(model.getHips())
                .shoeSize(model.getShoeSize())
                .eyes(model.getEyes())
                .isBooked(model.getIsBooked())
                .images(model.getImages())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }


}
