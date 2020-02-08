package com.blastingconcept.devcon.domain.profile;

import com.blastingconcept.devcon.domain.user.User;
import reactor.core.publisher.Mono;

public interface ProfileRepository {

    Mono<User> save(Profile profile);

}
