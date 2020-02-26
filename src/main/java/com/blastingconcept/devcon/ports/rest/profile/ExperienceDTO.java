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
public class ExperienceDTO {

    private String id;

    @NotNull(message = "Title is required")
    @NotEmpty(message = "Title cannot be empty")
    private String title;

    @NotNull(message = "Company is required")
    @NotEmpty(message = "Company cannot be empty")
    private String company;
    private String location;

    @NotNull(message = "From date is required")
    private String from;
    private String to;
    private Boolean current;
    private String description;
}
