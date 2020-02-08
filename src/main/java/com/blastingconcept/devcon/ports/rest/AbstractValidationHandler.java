package com.blastingconcept.devcon.ports.rest;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.MediaType;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

public abstract class AbstractValidationHandler<T,U extends Validator> {


    private final U validator;

    protected AbstractValidationHandler(U validator) {
        this.validator = validator;
    }

    protected Errors validateBody(T body) {
        Errors errors = new BeanPropertyBindingResult(body, body.getClass().getName());
        this.validator.validate(body, errors);
        return errors;
    }

    protected Mono<ServerResponse> onValidationErrors(Errors errors) {
        return ServerResponse.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(
                        AppResponseErrors.builder()
                            .errors(errors.getAllErrors().stream()
                                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                    .collect(Collectors.toList())).build()),
                        AppResponseErrors.class);
    }
}
