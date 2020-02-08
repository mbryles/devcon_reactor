package com.blastingconcept.devcon.domain.auth;

import com.blastingconcept.devcon.domain.user.User;
import reactor.core.publisher.Mono;

public interface AuthenticationService {

    Mono<String> register(User user);
    Mono<User> getAuthenticatedUser(String token);
    Mono<String> login(UserLogin userLogin);

}
