package com.modelling.guiservice.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ViewRequest implements Serializable, Cloneable {

    Map<String, Object> fieldSearchMap;
    String globalSearch;

    @NotNull(message = "Page cannot be empty")
    @Range(min = 0)
    Integer page;

    @NotNull(message = "PageSize cannot be empty")
    @Range(min = 0)
    Integer pageSize;

    String sortValue;

    String sortOrder;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


}
