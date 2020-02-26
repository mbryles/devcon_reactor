package com.blastingconcept.devcon.ports.rest.post;

import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private String id;
    private String userId;
    private String text;
    private String name;
    private String avatar;
    private Date date;
}
