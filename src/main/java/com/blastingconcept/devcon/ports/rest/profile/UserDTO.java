package com.blastingconcept.devcon.ports.rest.profile;

import lombok.*;

@Data
@Builder(access = AccessLevel.PUBLIC, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String id;
    private String name;
    private String avatar;
}
