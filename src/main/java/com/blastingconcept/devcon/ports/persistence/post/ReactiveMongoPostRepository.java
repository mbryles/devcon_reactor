package com.blastingconcept.devcon.ports.persistence.post;

import com.blastingconcept.devcon.domain.post.Post;
import com.blastingconcept.devcon.domain.post.PostRepository;
import com.blastingconcept.devcon.domain.user.User;
import com.blastingconcept.devcon.ports.persistence.post.MongoPost;
import com.blastingconcept.devcon.ports.persistence.user.MongoUser;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Date;

@Repository
public class ReactiveMongoPostRepository implements PostRepository {

    private ReactiveMongoTemplate reactiveMongoTemplate;

    public ReactiveMongoPostRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Post> save(Post post) {

//        MongoUser mongoUser = new MongoUser(user.getName(), user.getEmail(), user.getPassword(), user.getAvatar(),
//                new Date());
//        return reactiveMongoTemplate.save(mongoUser, "users")
//                .map(mu -> {
//                    return new User(mu.getId().toString(), mu.getName(), mu.getEmail(), "****", mu.getAvatar(), mu.getDate());
//                });


        Mono<MongoPost> mongoPost = reactiveMongoTemplate.findById(post.getUserId(), MongoUser.class, "users")
                .map( user -> {
                    return  MongoPost.builder()
                    .name(post.getName())
                    .avatar(post.getAvatar())
                    .text(post.getText())
                    .user(user)
                    .date(new Date())
                    .build();
                });

        return reactiveMongoTemplate.save(mongoPost, "posts")
                .map(mongoPost1 -> {

                    User user = User.builder()
                            .id(mongoPost1.getUser().getId().toString())
                            .avatar(mongoPost1.getUser().getAvatar())
                            .name(mongoPost1.getUser().getName())
                            .email(mongoPost1.getUser().getEmail())
                            .password(mongoPost1.getUser().getPassword())
                            .timeStamp(mongoPost1.getUser().getDate())
                            .build();

                    return Post.builder()
                            .id(mongoPost1.getId().toString())
                            .userId(user.getId())
                            .text(mongoPost1.getText())
                            .name(mongoPost1.getName())
                            .avatar(mongoPost1.getAvatar())
                            .date(mongoPost1.getDate())
                            .build();
                });

//        mongoUser.subscribe( mu -> {
//            MongoPost mongoPost = MongoPost.builder()
//                    .name(post.getName())
//                    .avatar(post.getAvatar())
//                    .text(post.getText())
//                    .user(mu)
//                    .build();
//
//
//        });


//        MongoPost mongoPost = MongoPost.builder()
//                                    .name(post.getName())
//                                    .avatar(post.getAvatar())
//                                    .text(post.getText())
//                                    .user(mongoUser.)
//
//        return reactiveMongoTemplate.save()

//                            User  mongoUserMono = reactiveMongoTemplate.findById(post.getUser().getId(), MongoUser.class,
//               "users")
//                .map( user -> {
//                                 return MongoPost.builder()
//                           .user(user)
//                           .avatar(post.getAvatar())
//                           .text(post.getText())
//                           .name(post.getName())
//                           .date(new Date())
//                           .build();
//        } )
//               .map( user -> {
//                     return MongoPost.builder()
//                           .user(user)
//                           .avatar(post.getAvatar())
//                           .text(post.getText())
//                           .name(post.getName())
//                           .date(new Date())
//                           .build();
//               })

//        MongoPost mongoPost = MongoPost.builder()
//                .avatar(post.getAvatar())
//                .text(post.getText())
//                .name(post.getName())
//                .date(new Date())
//                .build();
//
//        return null;
    }
}
