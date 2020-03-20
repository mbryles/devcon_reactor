package com.blastingconcept.devcon.domain.post;

import com.blastingconcept.devcon.domain.post.impl.Like;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private String id;
    private String userId;
    private String text;
    private String name;
    private String avatar;
    private List<Like> userLikes;
    private List<Comment> comments;
    private Date date;

}
