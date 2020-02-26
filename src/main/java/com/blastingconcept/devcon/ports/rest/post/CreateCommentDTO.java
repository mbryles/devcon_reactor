package com.blastingconcept.devcon.ports.rest.post;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@ToString
@Builder(toBuilder=true)
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDTO {

    @NotNull(message = "Text is required")
    @NotEmpty(message = "Text cannot be empty")
    private String text;
}
