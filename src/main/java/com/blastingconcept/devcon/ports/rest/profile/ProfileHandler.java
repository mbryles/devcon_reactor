package com.blastingconcept.devcon.ports.rest.profile;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public interface ProfileHandler {

    Mono<ServerResponse> create(ServerRequest request);
    Mono<ServerResponse> me(ServerRequest request);
    Mono<ServerResponse> allProfiles(ServerRequest request);
    Mono<ServerResponse> profileByUserId(ServerRequest request);
    Mono<ServerResponse> addExperience(ServerRequest request);
    Mono<ServerResponse> deleteExperience(ServerRequest request);
    Mono<ServerResponse> addEducation(ServerRequest request);
    Mono<ServerResponse> deleteEducation(ServerRequest request);
    Mono<ServerResponse> githubRepos(ServerRequest request);
    Mono<ServerResponse> deleteAccount(ServerRequest request);

}
