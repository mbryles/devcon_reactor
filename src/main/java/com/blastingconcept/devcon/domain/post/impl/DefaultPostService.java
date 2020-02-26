package com.blastingconcept.devcon.domain.post.impl;

import com.blastingconcept.devcon.domain.post.Comment;
import com.blastingconcept.devcon.domain.post.Post;
import com.blastingconcept.devcon.domain.post.PostRepository;
import com.blastingconcept.devcon.domain.post.PostService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DefaultPostService implements PostService {

    private PostRepository postRepository;

    public DefaultPostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public Mono<Post> savePost(Post post) {
        return this.postRepository.save(post);
    }

    @Override
    public Mono<Post> fetchPostById(String id) {
        return this.postRepository.findById(id);
    }

    @Override
    public Flux<Post> fetchAllPosts() {
        return this.postRepository.findAll();
    }

    @Override
    public Mono<Void> deletePost(String id) {
        return this.postRepository.deleteById(id);
    }

    @Override
    public Mono<Post> likePost(String postId, String userId) {
        return this.postRepository.findById(postId)
                .map(post -> {

                    HashSet<String> userLikesHash = (post.getUserLikes() != null) ?
                            new HashSet<>(post.getUserLikes()) :
                            new HashSet<>();

                    userLikesHash.add(userId);

                    return post.toBuilder()
                            .userLikes(new ArrayList<>(userLikesHash))
                            .build();
                })
                .flatMap(postToSave -> this.postRepository.save(postToSave));
    }

    @Override
    public Mono<Post> unlikePost(String postId, String userId) {
        return this.postRepository.findById(postId)
                .map(post -> {

                    if (post.getUserLikes() != null) {
                        Set<String> userLikesHash = new HashSet<>(post.getUserLikes());

                        userLikesHash.remove(userId);
                    }

                    return post.toBuilder()
                            .userLikes(new ArrayList<>())
                            .build();
                })
                .flatMap(postToSave -> this.postRepository.save(postToSave));
    }

    @Override
    public Mono<Post> addComment(String postId, Comment comment) {
        return this.postRepository.findById(postId)
                .map(post -> {

                    List<Comment> comments = new ArrayList<>(post.getComments());
                    comments.add(comment);
                    return post.toBuilder()
                            .comments(comments)
                            .build();
                })
                .flatMap(postToSave -> this.postRepository.save(postToSave));
    }

    @Override
    public Mono<Post> deleteComment(String postId, String commentId) {
        return this.postRepository.findById(postId)
                .map(post -> {
                    return post.toBuilder()
                            .comments(this.unshiftCommentsById(post.getComments(), commentId))
                            .build();
                })
                .flatMap(postToSave -> this.postRepository.save(postToSave));
    }


    private List<Comment> unshiftCommentsById(List<Comment> comments, String id) {

        return comments.stream()
                .filter(p -> ! p.getId().equals(id))
                .collect(Collectors.toList());

    }

}
