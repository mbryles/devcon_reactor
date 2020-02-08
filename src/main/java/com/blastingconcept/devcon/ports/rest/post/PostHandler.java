package com.blastingconcept.devcon.ports.rest.post;

import com.blastingconcept.devcon.domain.post.Post;
import com.blastingconcept.devcon.domain.post.PostRepository;
import com.blastingconcept.devcon.domain.user.User;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.server.ServerResponse.created;

@Component
public class PostHandler {

    private PostRepository postRepository;

    public PostHandler(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public Mono<ServerResponse> createPost(ServerRequest req)  {

        return req.body(BodyExtractors.toMono(Post.class))
                .flatMap(post -> this.postRepository.save(post))
                .flatMap(p -> created(URI.create("/posts/" + p.getId())).bodyValue(p));
    }
}
