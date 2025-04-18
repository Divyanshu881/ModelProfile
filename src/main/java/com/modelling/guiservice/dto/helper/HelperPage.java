package com.modelling.guiservice.dto.helper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class HelperPage<T> extends PageImpl<T> {

    public String status;
    public String message;

    public HelperPage() {
        super(new ArrayList<>());
    }

    public HelperPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public HelperPage(List<T> content) {
        super(content, PageRequest.of(0, 100000), content.size());
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public HelperPage(@JsonProperty("content") List<T> content,
                      @JsonProperty("number") int number,
                      @JsonProperty("size") int size,
                      @JsonProperty("totalElements") Long totalElements,
                      @JsonProperty("pageable") JsonNode pageable,
                      @JsonProperty("last") boolean last,
                      @JsonProperty("totalPages") int totalPages,
                      @JsonProperty("sort") JsonNode sort, @JsonProperty("first") boolean first,
                      @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "HelperPage{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", content=" + getContent() +
                ", pageable=" + getPageable() +
                ", total=" + getTotalElements() +
                '}';
    }


}
