package com.blastingconcept.devcon.ports.rest.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EducationDTO {

    private String school;
    private String degree;
    private String fieldOfStudy;
    private Date from;
    private Date to;
    private Boolean current;
    private String description;
}
