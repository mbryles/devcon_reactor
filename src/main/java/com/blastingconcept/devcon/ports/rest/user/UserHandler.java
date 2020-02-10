package com.blastingconcept.devcon.ports.rest.user;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface UserHandler {
    Mono<ServerResponse> getUserById(ServerRequest req);
}
