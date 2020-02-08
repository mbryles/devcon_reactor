package com.blastingconcept.devcon.domain.user;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id;
    private String name;
    private String email;
    private String password;
    private String avatar;
    private Date timeStamp;
}
