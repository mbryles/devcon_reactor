package com.blastingconcept.devcon.ports.rest.auth;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface AuthenticationHandler {

    Mono<ServerResponse> register(ServerRequest request);

    Mono<ServerResponse> login(ServerRequest request);

    Mono<ServerResponse> getAuthenticatedUser(ServerRequest request);
}
