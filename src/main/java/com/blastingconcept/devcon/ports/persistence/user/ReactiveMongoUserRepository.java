package com.blastingconcept.devcon.ports.persistence.user;

import com.blastingconcept.devcon.domain.user.User;
import com.blastingconcept.devcon.domain.user.UserRepository;
import com.blastingconcept.devcon.ports.persistence.user.MongoUser;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ReactiveMongoUserRepository implements UserRepository {

    private ReactiveMongoTemplate reactiveMongoTemplate;
    private PasswordEncoder passwordEncoder;

    public ReactiveMongoUserRepository(ReactiveMongoTemplate reactiveMongoTemplate, PasswordEncoder passwordEncoder) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<User> findById(String id) {

        return  reactiveMongoTemplate.findById(id, MongoUser.class,
                "users")
                .map(mongoUser -> {
                    return User.builder()
                            .id(mongoUser.getId().toString())
                            .name(mongoUser.getName())
                            .avatar(mongoUser.getAvatar())
                            .email(mongoUser.getEmail())
                            .password(mongoUser.getPassword())
                            .timeStamp(mongoUser.getDate())
                            .build();
                });
    }

    @Override
    public Mono<User> findByEmail(String email) {
        return  reactiveMongoTemplate.findOne(Query.query(Criteria.where("email").is(email)), MongoUser.class,
                "users")
                .map(mongoUser -> {
                    return User.builder()
                            .id(mongoUser.getId().toString())
                            .name(mongoUser.getName())
                            .avatar(mongoUser.getAvatar())
                            .email(mongoUser.getEmail())
                            .password(mongoUser.getPassword())
                            .timeStamp(mongoUser.getDate())
                            .build();
                });
    }

    @Override
    public Mono<User> save(User user) {
        MongoUser mongoUser = MongoUser.builder()
                                .name(user.getName())
                                .email(user.getEmail())
                                .date(user.getTimeStamp())
                                .password(passwordEncoder.encode(user.getPassword()))
                                .build();
        return reactiveMongoTemplate.save(mongoUser, "users")
                .map(mu -> {
                    return new User(mu.getId().toString(), mu.getName(), mu.getEmail(), "****", mu.getAvatar(), mu.getDate());
                });
    }

    @Override
    public Mono<Void> delete(User user) {

        return reactiveMongoTemplate.findAndRemove(new Query(Criteria.where("id").is(user.getId())), MongoUser.class,
                "users").then();
    }


}
