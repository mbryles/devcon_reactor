package com.blastingconcept.devcon.domain.post;

import com.blastingconcept.devcon.domain.user.User;
import lombok.*;

import java.util.Date;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private User user;
    private String text;
    private String name;
    private String avatar;
    private Date date;
}
