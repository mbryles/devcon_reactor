package com.blastingconcept.devcon.domain.profile;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Social {

    private String youtube;
    private String twitter;
    private String facebook;
    private String linkedin;
    private String instagram;
}
