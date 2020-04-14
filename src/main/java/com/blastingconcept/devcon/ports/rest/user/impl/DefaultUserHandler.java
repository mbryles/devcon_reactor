package com.blastingconcept.devcon.ports.rest.user.impl;

import com.blastingconcept.devcon.domain.user.UserRepository;
import com.blastingconcept.devcon.ports.rest.AppResponseErrors;
import com.blastingconcept.devcon.ports.rest.user.UserHandler;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class DefaultUserHandler implements UserHandler {

    private UserRepository userRepository;

    public DefaultUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> getUserById(ServerRequest req) {
        return this.userRepository.findById((req.pathVariable("id")))
                .flatMap(user -> ok().bodyValue(user))
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }
}
