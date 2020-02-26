package com.blastingconcept.devcon.ports.persistence.post;

import com.blastingconcept.devcon.domain.post.Comment;
import com.blastingconcept.devcon.domain.post.Post;
import com.blastingconcept.devcon.domain.post.PostRepository;
import com.blastingconcept.devcon.ports.persistence.user.MongoUser;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ReactiveMongoPostRepository implements PostRepository {

    private ReactiveMongoTemplate reactiveMongoTemplate;

    public ReactiveMongoPostRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Post> save(Post post) {

        return reactiveMongoTemplate.findById(post.getId(),MongoPost.class, "posts")
                .map( p -> {
                    return  MongoPost.builder()
                            .id(p.getId())
                            .name(post.getName())
                            .avatar(post.getAvatar())
                            .text(post.getText())
                            .userId(post.getUserId())
                            .comments(this.mapFromComment(post.getComments()))
                            .userLikes(post.getUserLikes())
                            .date(new Date())
                            .build();
                })
                .switchIfEmpty(Mono.just(MongoPost.builder()
                        .name(post.getName())
                        .avatar(post.getAvatar())
                        .text(post.getText())
                        .userId(post.getUserId())
                        .date(new Date())
                        .comments(this.mapFromComment(post.getComments()))
                        .userLikes(post.getUserLikes())
                        .build()))
                .flatMap(postToSave -> reactiveMongoTemplate.save(postToSave, "posts"))
                .map(p -> Post.builder()
                    .id(p.getId().toString())
                        .userId(p.getUserId())
                        .name(p.getName())
                        .avatar(p.getAvatar())
                        .date(p.getDate())
                        .comments(this.mapToCommment(p.getComments()))
                        .userLikes(p.getUserLikes())
                        .text(p.getText())
                        .build()
                );
    }

    @Override
    public Flux<Post> findAll() {
        return reactiveMongoTemplate.findAll(MongoPost.class, "posts")
                .map(mongoPost ->  Post.builder()
                        .id(mongoPost.getId().toString())
                        .date(mongoPost.getDate())
                        .avatar(mongoPost.getAvatar())
                        .name(mongoPost.getName())
                        .text(mongoPost.getText())
                        .comments(this.mapToCommment(mongoPost.getComments()))
                        .userId(mongoPost.getUserId())
                        .userLikes(mongoPost.getUserLikes())
                        .build()
                );
    }



    @Override
    public Mono<Post> findById(String id) {
        return reactiveMongoTemplate.findById(id, MongoPost.class)
                .map(mongoPost ->
                        Post.builder()
                                .id(mongoPost.getId().toString())
                                .userId(mongoPost.getUserId())
                                .comments(this.mapToCommment(mongoPost.getComments()))
                                .text(mongoPost.getText())
                                .name(mongoPost.getName())
                                .avatar(mongoPost.getAvatar())
                                .date(mongoPost.getDate())
                                .userLikes(mongoPost.getUserLikes())
                                .build()
                );
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return reactiveMongoTemplate.findAndRemove(new Query().addCriteria(Criteria.where("id").is(id)), MongoPost.class)
                .then();
    }

    private List<Comment> mapToCommment(List<MongoComment> comments) {
        return comments == null ? Collections.emptyList() : comments.stream()
                .map( comment -> com.blastingconcept.devcon.domain.post.Comment.builder()
                        .id(comment.getId())
                        .avatar(comment.getAvatar())
                        .date(comment.getDate())
                        .name(comment.getName())
                        .text(comment.getText())
                        .userId(comment.getUserId())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MongoComment> mapFromComment(List<Comment> comments) {
        return comments == null ? Collections.emptyList() : comments.stream()
                .map(comment -> MongoComment.builder()
                    .id(comment.getId())
                        .userId(comment.getUserId())
                        .text(comment.getText())
                        .name(comment.getName())
                        .date(comment.getDate())
                        .avatar(comment.getAvatar())
                        .build()
                )
                .collect(Collectors.toList());
    }


}
