package com.blastingconcept.devcon.ports.rest.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class SocialDTO {

    private String youtube;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;

}
