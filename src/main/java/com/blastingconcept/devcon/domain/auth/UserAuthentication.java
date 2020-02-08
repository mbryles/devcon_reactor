package com.blastingconcept.devcon.domain.auth;

import lombok.*;

@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthentication {

    private String email;
    private String password;
}
