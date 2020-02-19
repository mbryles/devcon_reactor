package com.blastingconcept.devcon.ports.persistence.profile;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "profiles")
@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class MongoProfile {
    @Id
    private ObjectId id;


    @Indexed(unique = true)
    private String userId;

    private String company;
    private String website;
    private String location;
    private String status;
    private List<String> skills;
    private String bio;
    private String gitHubUserName;

    private List<MongoExperience> experience;
    private List<MongoEducation> education;
    private MongoSocial social;
    private Date date;
}
