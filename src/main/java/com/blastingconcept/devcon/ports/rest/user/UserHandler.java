package com.blastingconcept.devcon.ports.rest.user;

import com.blastingconcept.devcon.domain.auth.InvalidCredentialsException;
import com.blastingconcept.devcon.domain.user.User;
import com.blastingconcept.devcon.domain.user.UserRepository;
import com.blastingconcept.devcon.ports.rest.AppResponseErrors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class UserHandler {

    private UserRepository userRepository;

    public UserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<ServerResponse> getUserById(ServerRequest req) {
        return this.userRepository.findById((req.pathVariable("id")))
                .flatMap(user -> ok().bodyValue(user))
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( new AppResponseErrors(List.of(t.getMessage()))));
    }

//    public Mono<ServerResponse> getUserByEmail(ServerRequest req) {
//        return this.userRepository.findById(String.valueOf(req.pathVariable("email")))
//                .flatMap(user -> ok().bodyValue(user))
//                .switchIfEmpty(notFound().build());
//    }
//
//    public Mono<ServerResponse> createUser(ServerRequest req) {
//        return req.body(BodyExtractors.toMono(User.class))
//                .flatMap(user -> this.userRepository.save(user))
//                .flatMap(u -> created(URI.create("/users/" + u.getEmail())).bodyValue(u));
//    }
}
