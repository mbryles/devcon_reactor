package com.blastingconcept.devcon.ports.rest.post;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface PostHandler {

    Mono<ServerResponse> create(ServerRequest request);
    Mono<ServerResponse> allPosts(ServerRequest request);
    Mono<ServerResponse> postById(ServerRequest request);
    Mono<ServerResponse> deleteById(ServerRequest request);
    Mono<ServerResponse> like(ServerRequest request);
    Mono<ServerResponse> unlike(ServerRequest request);
    Mono<ServerResponse> addComment(ServerRequest request);
    Mono<ServerResponse> deleteComment(ServerRequest request);



}
