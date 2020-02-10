package com.blastingconcept.devcon.ports.persistence.profile;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class MongoExperience {
    private String title;
    private String company;
    private String location;
    private Date from;
    private Date to;
    private Boolean current;
    private String description;

}
