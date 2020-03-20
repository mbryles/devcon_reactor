package com.blastingconcept.devcon.domain.post.impl;

import lombok.*;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class Like {

    private String id;
    private String user;
}
