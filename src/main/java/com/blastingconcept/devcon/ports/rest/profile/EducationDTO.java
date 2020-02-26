package com.blastingconcept.devcon.ports.rest.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {

    private String id;

    @NotNull(message = "School is required")
    @NotEmpty(message = "School cannot be empty")
    private String school;

    @NotNull(message = "Degree is required")
    @NotEmpty(message = "Degree cannot be empty")
    private String degree;

    @NotNull(message = "Field of study is required")
    @NotEmpty(message = "Field of study cannot be empty")
    private String fieldOfStudy;

    @NotNull(message = "From date is required")
    private String from;
    private String to;
    private Boolean current;
    private String description;
}
