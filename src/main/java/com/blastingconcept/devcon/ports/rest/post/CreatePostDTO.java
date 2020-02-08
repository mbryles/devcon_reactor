package com.blastingconcept.devcon.ports.rest.post;

import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostDTO {

    private String text;
}
