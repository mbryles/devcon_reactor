package com.blastingconcept.devcon.domain.profile;

import reactor.core.publisher.Mono;

public interface ProfileRepository {

    Mono<Profile> save(Profile profile);
    Mono<Profile> findByUserId(String id);
    Mono<Void> deleteByUserId(String id);
}
