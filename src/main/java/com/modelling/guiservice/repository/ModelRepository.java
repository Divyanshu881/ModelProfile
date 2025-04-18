package com.modelling.guiservice.repository;

import com.modelling.guiservice.model.ModelProfile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends ElasticsearchRepository<ModelProfile, String> {
    List<ModelProfile> findByName(String name);

    List<ModelProfile> findByGender(String gender);

    List<ModelProfile> findByAgeBetween(Integer minAge, Integer maxAge);

    List<ModelProfile> findByHeightBetween(Double minHeight, Double maxHeight);

    List<ModelProfile> findByIsBooked(Boolean isBooked);

}