package com.blastingconcept.devcon.ports.persistence.profile;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class MongoEducation {
    private String id;
    private String school;
    private String degree;
    private String fieldOfStudy;
    private Date from;
    private Date to;
    private Boolean current;
    private String description;
}
