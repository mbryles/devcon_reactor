package com.blastingconcept.devcon.domain.post;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id;
    private String userId;
    private String text;
    private String name;
    private String avatar;
    private Date date;
}
