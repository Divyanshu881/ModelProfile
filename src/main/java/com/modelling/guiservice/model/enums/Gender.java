package com.modelling.guiservice.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("Male"),
    FEMALE("Female");

    private final String label;

    Gender(String label) {
        this.label = label;
    }

    @JsonValue
    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Gender fromLabel(String value) {
        for (Gender gender : values()) {
            if (gender.label.equalsIgnoreCase(value) || gender.name().equalsIgnoreCase(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("Invalid gender: " + value);
    }
}
