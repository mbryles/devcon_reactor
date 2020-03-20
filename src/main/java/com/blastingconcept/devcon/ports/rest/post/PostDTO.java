package com.blastingconcept.devcon.ports.rest.post;

import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private String id;
    private String user;
    private String text;
    private String name;
    private String avatar;
    private List<LikeDTO> likes;
    private List<CommentDTO> comments;
    private Date date;

}
