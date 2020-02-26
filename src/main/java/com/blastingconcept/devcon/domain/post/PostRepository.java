package com.blastingconcept.devcon.domain.post;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostRepository {
    Mono<Post> save(Post post);
    Flux<Post> findAll();
    Mono<Post> findById(String id);
    Mono<Void> deleteById(String id);

}
