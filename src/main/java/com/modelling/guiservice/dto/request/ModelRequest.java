package com.modelling.guiservice.dto.request;


import com.modelling.guiservice.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Age is required")
    @Min(value = 16, message = "Model must be at least 16 years old")
    @Max(value = 99, message = "Age must be less than 100")
    private String age;

    @NotNull(message = "Height is required")
    @DecimalMin(value = "100", message = "Height must be at least 100 cm")
    @DecimalMax(value = "250", message = "Height must be less than 250 cm")
    private String height;

    @NotNull(message = "Gender is required")
    private Gender gender; // "MALE" or "FEMALE"

    // Female-specific
    private String bust;
    private String waist;

    // Male-specific
    private String chest;
    private String lowerWaist;

    // Common
    private String hips;
    private String shoeSize;
    private String eyes;

    @NotNull(message = "At least one image is required")
    @Size(min = 1, max = 10, message = "You can upload 1-10 images")
    private List<MultipartFile> images;
}