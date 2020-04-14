package com.blastingconcept.devcon.domain.post;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PostService {

    Mono<Post> savePost(Post post);
    Mono<Post> fetchPostById(String id);
    Flux<Post> fetchAllPosts();
    Mono<Void> deletePost(String id);
    Mono<Post> likePost(String postId, String userId);
    Mono<Post> unlikePost(String postId, String userId);
    Mono<Post> addComment(String postId, Comment comment);
    Mono<Post> deleteComment(String postId, String commentId);
    Mono<Void> deleteAllUserPosts(String userId);

}
