package com.blastingconcept.devcon.ports.rest.post.impl;

import com.blastingconcept.devcon.domain.post.Comment;
import com.blastingconcept.devcon.domain.post.Post;
import com.blastingconcept.devcon.domain.post.PostRepository;
import com.blastingconcept.devcon.domain.post.PostService;
import com.blastingconcept.devcon.domain.profile.Education;
import com.blastingconcept.devcon.domain.profile.Profile;
import com.blastingconcept.devcon.domain.user.User;
import com.blastingconcept.devcon.ports.rest.AbstractValidationHandler;
import com.blastingconcept.devcon.ports.rest.AppResponseErrors;
import com.blastingconcept.devcon.ports.rest.post.*;
import com.blastingconcept.devcon.ports.rest.profile.EducationDTO;
import com.blastingconcept.devcon.ports.rest.profile.UpsertProfileDTO;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class DefaultPostHandler extends AbstractValidationHandler implements PostHandler {

    private PostService postService;

    public DefaultPostHandler(Validator validator, PostService postService) {
        super(validator);
        this.postService = postService;
    }

    public Mono<ServerResponse> create(ServerRequest request)  {

        return request.bodyToMono(CreatePostDTO.class)
                .flatMap(createPostDTO -> {
                    Errors errors = validateBody(createPostDTO);

                    if (errors == null || errors.getAllErrors()
                            .isEmpty()) {

                        return this.postService.savePost(Post.builder()
                            .date(new Date())
                                .text(createPostDTO.getText())
                                .userId(this.extractUserAttribute(request, "id"))
                                .avatar(this.extractUserAttribute(request, "avatar"))
                                .name(this.extractUserAttribute(request, "name"))
                                .date(new Date())
                                .build()
                        )
                        .flatMap( s -> ServerResponse.ok().bodyValue(PostDTO.builder()
                                .id(s.getId())
                                .userId(s.getUserId())
                                .text(s.getText())
                                .date(s.getDate())
                                .name(s.getName())
                                .avatar(s.getAvatar())
                                .build()
                        ));

                    } else {
                        return onValidationErrors(errors);
                    }
                });
    }

    @Override
    public Mono<ServerResponse> allPosts(ServerRequest request) {
        Flux<PostDTO> profileFlux = this.postService.fetchAllPosts()
                .map(post -> PostDTO.builder()
                        .userId(post.getUserId())
                        .id(post.getId())
                        .text(post.getText())
                        .date(post.getDate())
                        .avatar(post.getAvatar())
                        .comments(this.mapFromComments(post.getComments()))
                        .name(post.getName())
                        .userLikes(post.getUserLikes())
                        .build()
                );
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileFlux, Profile.class)
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    @Override
    public Mono<ServerResponse> postById(ServerRequest request) {
        return this.postService.fetchPostById(request.pathVariable("postId"))
                .flatMap(post -> ok().bodyValue(PostDTO.builder()
                        .name(post.getName())
                        .userLikes(post.getUserLikes())
                        .avatar(post.getAvatar())
                        .date(post.getDate())
                        .text(post.getText())
                        .id(post.getId())
                        .userId(post.getUserId())
                        .comments(this.mapFromComments(post.getComments()))
                        .build()
                ))
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    @Override
    public Mono<ServerResponse> deleteById(ServerRequest request) {
        return this.postService.deletePost(request.pathVariable("postId"))
                .flatMap(p -> ok().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    @Override
    public Mono<ServerResponse> like(ServerRequest request) {
        return this.postService.fetchPostById(request.pathVariable("postId"))
                .flatMap(post -> this.postService.likePost(post.getId(), this.extractUserAttribute(request, "id"))
                .flatMap(p -> ok().bodyValue(PostDTO.builder()
                        .name(p.getName())
                        .userLikes(p.getUserLikes())
                        .avatar(p.getAvatar())
                        .date(p.getDate())
                        .text(p.getText())
                        .id(p.getId())
                        .userId(p.getUserId())
                        .comments(this.mapFromComments(p.getComments()))
                        .build()
                ))
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build())));

    }

    @Override
    public Mono<ServerResponse> unlike(ServerRequest request) {
        return this.postService.fetchPostById(request.pathVariable("postId"))
                .flatMap(post -> this.postService.unlikePost(post.getId(), this.extractUserAttribute(request, "id"))
                        .flatMap(p -> ok().bodyValue(PostDTO.builder()
                                .name(p.getName())
                                .userLikes(p.getUserLikes())
                                .avatar(p.getAvatar())
                                .date(p.getDate())
                                .text(p.getText())
                                .id(p.getId())
                                .userId(p.getUserId())
                                .comments(this.mapFromComments(p.getComments()))
                                .build()
                        ))
                        .switchIfEmpty(notFound().build())
                        .onErrorResume(Exception.class,
                                t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                        .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build())));
    }

    @Override
    public Mono<ServerResponse> addComment(ServerRequest request) {

        return request.bodyToMono(CreateCommentDTO.class)
                .flatMap(commentDTO -> {
                    Errors errors = validateBody(commentDTO);

                    if (errors == null || errors.getAllErrors()
                            .isEmpty()) {

                        return  this.postService.addComment(request.pathVariable("postId"), Comment.builder()
                                .id(UUID.randomUUID().toString())
                                .userId(this.extractUserAttribute(request, "id"))
                                .text(commentDTO.getText())
                                .name(this.extractUserAttribute(request, "name"))
                                .avatar(this.extractUserAttribute(request, "avatar"))
                                .date(new Date())
                                .build())

                                .flatMap(post -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(
                                                PostDTO.builder()
                                                        .userId(post.getUserId())
                                                        .id(post.getId())
                                                        .text(post.getText())
                                                        .comments(this.mapFromComments(post.getComments()))
                                                        .date(post.getDate())
                                                        .avatar(post.getAvatar())
                                                        .userLikes(post.getUserLikes())
                                                        .name(post.getName())
                                                        .build()
                                        )
                                        .onErrorResume(Exception.class,
                                                t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build())));

                    } else {
                        return onValidationErrors(errors);
                    }
                });
    }

    @Override
    public Mono<ServerResponse> deleteComment(ServerRequest request) {

        return this.postService.deleteComment(request.pathVariable("postId"), request.pathVariable("commentId"))
                .flatMap(p -> ok().build())
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }


    private List<CommentDTO> mapFromComments(List<Comment> comments) {

        return comments == null ? null : comments.stream()
                .map(comment -> CommentDTO.builder()
                    .avatar(comment.getAvatar())
                        .date(comment.getDate())
                        .id(comment.getId())
                        .name(comment.getName())
                        .text(comment.getText())
                        .userId(comment.getUserId())
                        .build()
                )
                .collect(Collectors.toList());

    }

    private String extractUserAttribute(ServerRequest request, String key) {

        LinkedHashMap<String,Object> attributeMap = (LinkedHashMap<String, Object>) request.attributes().get("user");
        return (String) attributeMap.get(key);

    }
}
