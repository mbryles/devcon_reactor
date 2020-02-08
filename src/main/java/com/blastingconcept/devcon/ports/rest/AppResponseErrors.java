package com.blastingconcept.devcon.ports.rest;

import lombok.*;

import java.util.List;

@Data
@ToString
@Builder(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class AppResponseErrors {

    private List<String> errors;
}
