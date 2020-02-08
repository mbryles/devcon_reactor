package com.blastingconcept.devcon.ports.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
public class AppResponseErrors {

    private List<String> errors;
}
