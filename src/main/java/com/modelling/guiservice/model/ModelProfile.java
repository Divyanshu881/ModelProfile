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

    @Field(type = FieldType.Keyword, name = "name")
    private String name;

    @Field(type = FieldType.Keyword, name = "age")
    private String age;

    @Field(type = FieldType.Keyword, name = "height")
    private String height; // in cm

    @Field(type = FieldType.Keyword, name = "gender")
    private Gender gender; // "MALE" or "FEMALE"

    // Measurements - different for male/female
    @Field(type = FieldType.Keyword, name = "bust")
    private String bust; // for female

    @Field(type = FieldType.Keyword, name = "chest")
    private String chest; // for male

    @Field(type = FieldType.Keyword, name = "waist")
    private String waist; // for female

    @Field(type = FieldType.Keyword, name = "lowerWaist")
    private String lowerWaist; // for male

    @Field(type = FieldType.Keyword, name = "hips")
    private String hips;

    @Field(type = FieldType.Keyword, name = "shoeSize")
    private String shoeSize;

    @Field(type = FieldType.Keyword, name = "eyes")
    private String eyes;

    @Field(type = FieldType.Boolean, name = "isBooked")
    private Boolean isBooked;

    @Field(type = FieldType.Nested, name = "images")
    private List<ModelImage> images;

    @Field(type = FieldType.Date,
            format = {},
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date,
            format = {},
            pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}

