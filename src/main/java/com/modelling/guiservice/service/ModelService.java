package com.modelling.guiservice.service;

import com.modelling.guiservice.dto.helper.HelperPage;
import com.modelling.guiservice.dto.request.ModelRequest;
import com.modelling.guiservice.dto.request.ViewRequest;
import com.modelling.guiservice.dto.response.ModelResponse;

public interface ModelService {

    ModelResponse createModel(ModelRequest request);

    HelperPage<ModelResponse> searchModels(ViewRequest viewRequest) throws Exception;


    void deleteModel(String id);

    ModelResponse updateModel(String id, ModelRequest request);

    ModelResponse getModelById(String id);
}
