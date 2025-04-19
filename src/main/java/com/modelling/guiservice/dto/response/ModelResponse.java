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
    private String age;
    private String height;
    private Gender gender;
    private String bust;
    private String chest;
    private String waist;
    private String lowerWaist;
    private String hips;
    private String shoeSize;
    private String eyes;
    private Boolean isBooked;
    private List<ModelImage> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
