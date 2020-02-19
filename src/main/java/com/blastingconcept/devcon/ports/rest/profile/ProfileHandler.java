package com.blastingconcept.devcon.ports.rest.profile;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProfileHandler {

    Mono<ServerResponse> create(ServerRequest request);
    Mono<ServerResponse> me(ServerRequest request);
    Mono<ServerResponse> allProfiles(ServerRequest request);
}
