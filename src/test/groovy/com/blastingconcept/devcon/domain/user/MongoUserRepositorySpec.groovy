package com.blastingconcept.devcon.domain.user

import com.blastingconcept.devcon.domain.user.User
import com.blastingconcept.devcon.domain.user.UserRepository
import com.blastingconcept.devcon.domain.user.impl.ReactiveMongoUserRepository
import com.blastingconcept.devcon.ports.persistence.user.MongoUser
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

@Configuration
class MongoUserRepositorySpec extends Specification {

    @Subject
    UserRepository userRepository

    PasswordEncoder passwordEncoder

    @Shared
    MongoClient reactiveMongoClient

    @Shared
    ReactiveMongoTemplate template


    def setup() {

        reactiveMongoClient =  MongoClients.create("mongodb://localhost:27017/")

        template =  new ReactiveMongoTemplate(reactiveMongoClient, "devcon_test")

        ((MappingMongoConverter)template.getConverter()).setTypeMapper(new DefaultMongoTypeMapper(null));

        passwordEncoder = new BCryptPasswordEncoder()

        userRepository = new ReactiveMongoUserRepository(template, passwordEncoder)
    }

    def cleanup() {

        template.findAllAndRemove(new Query(), MongoUser.class).blockLast()
    }

    def setupSpec() {
        println "Setup specification"
    }

    def cleanupSpec() {
        println "Clean up specification"
        template.dropCollection("users").block()
    }

    def 'verify UserRepository is not null'() {
        expect:
            userRepository != null
    }

    def 'verify create user'() {
        given:
            User user = User.builder()
                .name("testy mctesterson")
                .email("testy@test.com")
                .password("test123")
                .build()

        Mono<User> savedUser = userRepository.save(user)

        StepVerifier
                .create(savedUser)
                .expectNextCount(1)
                .expectComplete()
                .verify()


    }

    def 'verify call to save duplicate user throws exception'() {
        given:
            User user = User.builder()
                .name("testy two mctesterson")
                .email("testytwo@test.com")
                .password("test123")
                .build()

            Mono<User> savedUser = userRepository.save(user)

            StepVerifier
                .create(savedUser)
                .expectNextCount(1)
                .expectComplete()
                .verify()

        when:

            User userDupe = User.builder()
                    .name("testy two  mctesterson")
                    .email("testytwo@test.com")
                    .password("test123")
                    .build()

        then:
            Mono<User> saveUser = userRepository.save(userDupe)

        StepVerifier
                .create(saveUser)
                .expectError(DuplicateKeyException.class)
                .verify()
    }

    def 'verify call to fetch a user by id succeeds '() {

        given:
            User user = User.builder()
                .name("testy three  mctesterson")
                .email("testythree@test.com")
                .password("test123")
                .build()

        Mono<User> savedUser = userRepository.save(user)

        StepVerifier
            .create(savedUser)
            .expectNextCount(1)
            .expectComplete()
            .verify()


        Mono<User> getUser = savedUser
                                .flatMap({ u -> userRepository.findById(u.getId()) })


        StepVerifier
                .create(getUser)
                .expectNextCount(1)
                .expectComplete()
                .verify()
    }

    def 'verify call to fetch a user by id fails when user not found'() {
        given:

            Mono<User> getUser = userRepository.findById("123456789")

        StepVerifier
                .create(getUser)
                .verifyComplete()
    }

    def 'verify call to fetch a user by email succeeds '() {

        given:
            User user = User.builder()
                .name("testy four mctesterson")
                .email("testyfour@test.com")
                .password("test123")
                .build()

        Mono<User> savedUser = userRepository.save(user)

        StepVerifier
                .create(savedUser)
                .expectNextCount(1)
                .expectComplete()
                .verify()


        Mono<User> getUserByEmail = savedUser
                .flatMap({ u -> userRepository.findByEmail(u.getEmail()) })


        StepVerifier
                .create(getUserByEmail)
                .expectNextCount(1)
                .expectComplete()
                .verify()
    }
}
