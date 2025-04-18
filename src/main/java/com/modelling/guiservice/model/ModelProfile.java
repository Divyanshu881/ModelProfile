package com.modelling.guiservice.model;

import com.modelling.guiservice.model.enums.Gender;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "model_profiles")
public class ModelProfile {
    @Id
    private String id;

    @Field(type = FieldType.Text, name = "name")
    private String name;

    @Field(type = FieldType.Integer, name = "age")
    private Integer age;

    @Field(type = FieldType.Double, name = "height")
    private Double height; // in cm

    @Field(type = FieldType.Keyword, name = "gender")
    private Gender gender; // "MALE" or "FEMALE"

    // Measurements - different for male/female
    @Field(type = FieldType.Double, name = "bust")
    private Double bust; // for female

    @Field(type = FieldType.Double, name = "chest")
    private Double chest; // for male

    @Field(type = FieldType.Double, name = "waist")
    private Double waist; // for female

    @Field(type = FieldType.Double, name = "lowerWaist")
    private Double lowerWaist; // for male

    @Field(type = FieldType.Double, name = "hips")
    private Double hips;

    @Field(type = FieldType.Double, name = "shoeSize")
    private Double shoeSize;

    @Field(type = FieldType.Text, name = "eyes")
    private String eyes;

    @Field(type = FieldType.Boolean, name = "isBooked")
    private Boolean isBooked;

    @Field(type = FieldType.Nested, name = "images")
    private List<ModelImage> images;

    @Field(type = FieldType.Date, name = "createdAt")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, name = "updatedAt")
    private LocalDateTime updatedAt;
}

