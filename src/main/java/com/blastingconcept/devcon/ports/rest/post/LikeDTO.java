package com.blastingconcept.devcon.ports.rest.post;

import lombok.*;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class LikeDTO {
    String id;
    String user;
}
