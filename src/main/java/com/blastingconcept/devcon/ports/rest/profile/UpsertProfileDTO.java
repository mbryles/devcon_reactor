package com.blastingconcept.devcon.ports.rest.profile;

import com.blastingconcept.devcon.domain.profile.Education;
import com.blastingconcept.devcon.domain.profile.Experience;
import com.blastingconcept.devcon.domain.profile.Social;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UpsertProfileDTO {

    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name cannot be empty")
    private String status;

    @NotNull(message = "Name is required")
    @NotEmpty(message = "Name cannot be empty")
    private List<String> skills;

    private String company;
    private String website;
    private String location;
    private String bio;
    private String gitHubUserName;
    private List<ExperienceDTO> experience;
    private List<EducationDTO> education;
    private SocialDTO social;

}
