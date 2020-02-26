package com.blastingconcept.devcon.domain.profile;


import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class Education {

    private String id;
    private String school;
    private String degree;
    private String fieldOfStudy;
    private Date from;
    private Date to;
    private Boolean current;
    private String description;
}
