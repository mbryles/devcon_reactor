package com.blastingconcept.devcon.ports.persistence.profile;

import lombok.*;

@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class MongoSocial {

    private String youtube;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;
}
