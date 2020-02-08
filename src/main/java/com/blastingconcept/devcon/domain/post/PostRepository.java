package com.blastingconcept.devcon.domain.post;

import reactor.core.publisher.Mono;

public interface PostRepository {
    Mono<Post> save(Post post);
}
