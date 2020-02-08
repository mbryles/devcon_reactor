package com.blastingconcept.devcon.domain.auth;

public class InvalidCredentialsException extends Exception {

    public InvalidCredentialsException(String errorMessage) {
        super(errorMessage);
    }
}
