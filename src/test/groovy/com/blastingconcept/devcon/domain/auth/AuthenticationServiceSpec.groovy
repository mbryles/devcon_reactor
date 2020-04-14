package com.blastingconcept.devcon.domain.auth

import com.blastingconcept.devcon.domain.auth.impl.DefaultAuthenticationService
import com.blastingconcept.devcon.domain.post.PostService
import com.blastingconcept.devcon.domain.profile.Profile
import com.blastingconcept.devcon.domain.profile.ProfileService
import com.blastingconcept.devcon.domain.user.User
import com.blastingconcept.devcon.domain.user.UserRepository
import io.jsonwebtoken.SignatureAlgorithm
import org.bson.types.ObjectId
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification
import spock.lang.Subject

import javax.crypto.spec.SecretKeySpec
import javax.xml.bind.DatatypeConverter
import java.security.Key

class AuthenticationServiceSpec extends Specification{

    @Subject
    AuthenticationService authenticationService

    UserRepository userRepository = Mock()
    ProfileService profileService = Mock()
    PostService postService = Mock()

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder()

    def setup() {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes =
                DatatypeConverter.parseBase64Binary("fKd4KCkG9DBJPuMvuxTTYqjQzDyJtMwHKP8yjzaTSvB2uvH2uP");
        Key key = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        authenticationService =  new DefaultAuthenticationService(key, userRepository, passwordEncoder, profileService, postService)
    }

    def 'authentication service is not null'() {

        expect:
            authenticationService != null
    }

    def 'verify authentication service accepts valid user and returns a string'() {

        given:
            def user = User.builder()
                            .email("test@gmail.com")
                            .name("Tony Stark")
                            .password("iamironman")
                            .build()

        when:

            def token = authenticationService.register(user)

        then:

            1 * userRepository.save(user) >> Mono.just(
                    User.builder()
                            .id(ObjectId.get().toString())
                            .name(user.getName())
                            .email(user.getEmail())
                            .password(user.getPassword())
                            .build()
            )

            StepVerifier
                .create(token)
                .expectNextCount(1)
                .expectComplete()
                .verify();

    }

    def 'verify authentication service accepts valid user login object and returns a token'() {
        given:

            def userLogin = UserLogin.builder()
                    .email("test@gmail.com")
                    .password("iamironman")
                    .build()

        when:

            def token = authenticationService.login(userLogin)

        then:

            1 * userRepository.findByEmail(spock.lang.Specification._) >> Mono.just(
                    User.builder()
                            .email("test@gmail.com")
                            .name("Tony Stark")
                            .password(passwordEncoder.encode("iamironman"))
                            .build()
            )

        StepVerifier
                .create(token)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    def 'verify authentication service throws invalid credentials exception given mismatched credentials '() {
        given:

        def userLogin = UserLogin.builder()
                .email("test@gmail.com")
                .password("iamironn")
                .build()

        when:

        def token = authenticationService.login(userLogin)

        then:

        1 * userRepository.findByEmail(spock.lang.Specification._) >> Mono.just(
                User.builder()
                        .email("test@gmail.com")
                        .name("Tony Stark")
                        .password(passwordEncoder.encode("iamironman"))
                        .build()
        )

        StepVerifier
                .create(token)
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

}
