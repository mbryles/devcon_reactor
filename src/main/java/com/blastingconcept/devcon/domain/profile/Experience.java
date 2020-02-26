package com.blastingconcept.devcon.domain.profile;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class Experience {

    private String id;
    private String title;
    private String company;
    private String location;
    private Date from;
    private Date to;
    private Boolean current;
    private String description;
}
