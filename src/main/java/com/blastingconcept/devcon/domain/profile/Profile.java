package com.blastingconcept.devcon.domain.profile;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private String userId;
    private String company;
    private String website;
    private String location;
    private String status;
    private List<String> skills;
    private String bio;
    private String gitHubUserName;
    private List<Experience> experience;
    private List<Education> education;
    private Social social;
    private Date date;
}
