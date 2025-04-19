package com.modelling.guiservice.model;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelImage {
    @Field(type = FieldType.Keyword, name = "url")
    private String url;

    @Field(type = FieldType.Keyword, name = "type") // "PORTFOLIO", "PROFILE", etc.
    private String type;

    @Field(type = FieldType.Integer, name = "order")
    private Integer order;
}
