package com.blastingconcept.devcon.domain.user;

import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> findById(String id);
    Mono<User> findByEmail(String email);
    Mono<User> save(User user);
    Mono<Void> delete(User user);
}