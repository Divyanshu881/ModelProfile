package com.modelling.guiservice.dto.helper;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ElasticSearchResult<T> {
    private List<T> results;
    private long totalCount;

    public ElasticSearchResult(List<T> results, long totalCount) {
        this.results = results;
        this.totalCount = totalCount;
    }

}
