package com.blastingconcept.devcon.ports.persistence.post;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor
@AllArgsConstructor
public class MongoComment {

    private String id;
    private String userId;
    private String text;
    private String name;
    private String avatar;
    private Date date;
}
