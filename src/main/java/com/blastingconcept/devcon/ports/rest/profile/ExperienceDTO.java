package com.blastingconcept.devcon.ports.rest.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class ExperienceDTO {

    private String title;
    private String company;
    private String location;
    private Date from;
    private Date to;
    private Boolean current;
    private String description;
}
