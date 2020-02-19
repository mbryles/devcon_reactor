package com.blastingconcept.devcon.ports.rest.profile;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder(access = AccessLevel.PUBLIC, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UpsertProfileDTO {

    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name cannot be empty")
    private String status;

    @NotNull(message = "Skills is required")
    @NotEmpty(message = "Skills cannot be empty")
    private String skills;

    private String company;
    private String website;
    private String location;
    private String bio;
    private String gitHubUserName;
    private List<ExperienceDTO> experience;
    private List<EducationDTO> education;
    private SocialDTO social;

}
