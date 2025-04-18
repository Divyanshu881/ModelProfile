package com.modelling.guiservice.dto.response;

import com.modelling.guiservice.model.ModelImage;
import com.modelling.guiservice.model.enums.Gender;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelResponse {
    private String id;
    private String name;
    private Integer age;
    private Double height;
    private Gender gender;
    private Double bust;
    private Double chest;
    private Double waist;
    private Double lowerWaist;
    private Double hips;
    private Double shoeSize;
    private String eyes;
    private Boolean isBooked;
    private List<ModelImage> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
