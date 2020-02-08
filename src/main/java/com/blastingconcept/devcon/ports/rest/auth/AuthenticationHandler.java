package com.blastingconcept.devcon.ports.rest.auth;

import com.blastingconcept.devcon.domain.auth.AuthenticationService;
import com.blastingconcept.devcon.domain.auth.InvalidCredentialsException;
import com.blastingconcept.devcon.domain.auth.UserLogin;
import com.blastingconcept.devcon.domain.user.User;
import com.blastingconcept.devcon.ports.rest.AbstractValidationHandler;
import com.blastingconcept.devcon.ports.rest.AppResponseErrors;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class AuthenticationHandler extends AbstractValidationHandler {

    private AuthenticationService authenticationService;

    public AuthenticationHandler(AuthenticationService authenticationService, Validator validator) {
        super(validator);
        this.authenticationService = authenticationService;
    }

    public Mono<ServerResponse> register(ServerRequest request) {
        return request.bodyToMono(RegisterUserDTO.class)
                .flatMap(body -> {
                    Errors errors = validateBody(body);

                    if (errors == null || errors.getAllErrors()
                            .isEmpty()) {
                        return this.authenticationService.register(User.builder()
                                .name(body.getName())
                                .password(body.getPassword())
                                .email(body.getEmail())
                                .timeStamp(new Date())
                                .build())
                                .flatMap(s -> ServerResponse.ok().bodyValue(new TokenDTO(s)))
                                .onErrorResume(DuplicateKeyException.class,
                                        t -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                                .bodyValue( AppResponseErrors.builder().errors(List.of("User already exists")).build()));
                    } else {
                        return onValidationErrors(errors);
                    }
                });
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        return request.bodyToMono(LoginDTO.class)
                .flatMap(body -> {
                    Errors errors = validateBody(body);

                    if (errors == null || errors.getAllErrors().isEmpty()) {
                        return this.authenticationService.login(
                                UserLogin.builder()
                                        .email(body.getEmail())
                                        .password(body.getPassword())
                                        .build())
                                .flatMap(s -> ServerResponse.ok().bodyValue(new TokenDTO(s)))
                                .onErrorResume(InvalidCredentialsException.class,
                                        t -> ServerResponse.status(HttpStatus.BAD_REQUEST)
                                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
                    } else {
                        return onValidationErrors(errors);
                    }
                });
    }

    public Mono<ServerResponse> getAuthenticatedUser(ServerRequest request) {

        return ServerResponse.ok().bodyValue(request.attribute("user")
                .map(s -> {
                    LinkedHashMap<String,Object> um = (LinkedHashMap<String,Object>)s;
                    um.put("password", "****");
                    return um;
                }))
                .onErrorResume(SignatureException.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }


}

