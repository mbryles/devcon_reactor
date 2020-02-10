package com.blastingconcept.devcon.ports.persistence.profile

import com.blastingconcept.devcon.domain.profile.Education
import com.blastingconcept.devcon.domain.profile.Profile
import com.blastingconcept.devcon.domain.profile.ProfileRepository
import com.blastingconcept.devcon.domain.user.User
import com.blastingconcept.devcon.ports.persistence.user.MongoUser
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.types.ObjectId
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class MongoProfileRepositorySpec extends Specification{

    @Subject
    ProfileRepository profileRepository

    @Shared
    MongoClient reactiveMongoClient

    @Shared
    ReactiveMongoTemplate template

    def setup() {

        reactiveMongoClient =  MongoClients.create("mongodb://localhost:27017/")

        template =  new ReactiveMongoTemplate(reactiveMongoClient, "devcon_test")

        ((MappingMongoConverter)template.getConverter()).setTypeMapper(new DefaultMongoTypeMapper(null));

        profileRepository = new ReactiveMongoProfileRepository(template)

    }

    def cleanup() {

        template.findAllAndRemove(new Query(), MongoProfile.class)
            .flatMap({ mongoProfile ->
                template.findAndRemove(new Query().addCriteria(Criteria.where("_id")
                        .is(mongoProfile.getUserId())), MongoUser.class)
            }).blockLast()
    }

    def setupSpec() {
        println "Setup specification"
    }

    def cleanupSpec() {
        println "Clean up specification"
        template.dropCollection("users").block()
        template.dropCollection("profiles").block()
    }


    def 'verify ProfileRepository is not null'() {
        expect:
            profileRepository != null
    }

    def 'verify create Profile'() {

        given:

            LocalDateTime start = LocalDateTime.of(2011, 8, 22,0,0,0)
            LocalDateTime end = LocalDateTime.of(2015,5,17,0,0,0)
            Education education = Education.builder()
                .current(false)
                .degree("BS")
                .fieldOfStudy("Computer Science")
                .school("University of Georgia")
                .description("i got a degree")
                .from(start.toDate())
                .to(end.toDate())
                .build()

            Mono<Profile> profile = template.save(MongoUser.builder()
                    .name("testy mctesterson")
                    .email("testy@test.com")
                    .password("test123")
                    .build(), "users")
                .flatMap({ u ->
                    return Mono.just(Profile.builder()
                        .userId(u.getId().toString())
                        .company("ACME")
                        .status("Software Developer")
                        .website("www.acme.com")
                        .skills(List.of("Java", "Javascript", "Scala", "HTML", "CSS",
                                        "Python"))
                        .location("Atlanta, GA")
                        .bio("I do code")
                        .gitHubUserName("testymac")
                        .education(List.of(education))
                        .build())
                    })
                .flatMap({ profile -> profileRepository.save(profile) })

        StepVerifier
            .create(profile)
            .expectNextCount(1)
            .expectComplete()
            .verify()

    }

    def 'verify call to save duplicate Profile throws exception'() {

        given:
        LocalDateTime start = LocalDateTime.of(2011, 8, 22, 0, 0, 0)
        LocalDateTime end = LocalDateTime.of(2015, 5, 17, 0, 0, 0)
        Education education = Education.builder()
                .current(false)
                .degree("BS")
                .fieldOfStudy("Computer Science")
                .school("University of Georgia")
                .description("i got a degree")
                .from(start.toDate())
                .to(end.toDate())
                .build()

        Mono<Profile> profile = template.save(MongoUser.builder()
                .name("testy mctesterson")
                .email("testy@test.com")
                .password("test123")
                .build(), "users")
                .map({ mu ->
                    User.builder()
                            .id(mu.getId().toString())
                            .name(mu.getName())
                            .password(mu.getPassword())
                            .email(mu.getEmail())
                            .build()
                })
                .flatMap({ u ->
                    return Mono.just(Profile.builder()
                            .userId(u.getId().toString())
                            .company("ACME")
                            .status("Software Developer")
                            .website("www.acme.com")
                            .skills(List.of("Java", "Javascript", "Scala", "HTML", "CSS",
                                    "Python"))
                            .location("Atlanta, GA")
                            .bio("I do code")
                            .gitHubUserName("testymac")
                            .education(List.of(education))
                            .build())
                })
                .flatMap({ profile -> profileRepository.save(profile) })

        StepVerifier
                .create(profile)
                .expectNextCount(1)
                .expectComplete()
                .verify()

        Mono<Profile> savedProfile = profile
                .flatMap({ p ->
                    Mono.just(Profile.builder()
                            .userId(p.getUserId())
                            .company("ACME")
                            .status("Software Developer")
                            .website("www.acme.com")
                            .skills(List.of("Java", "Javascript", "Scala", "HTML", "CSS",
                                    "Python"))
                            .location("Atlanta, GA")
                            .bio("I do code")
                            .gitHubUserName("testymac")
                            .education(List.of(education))
                            .build())
                })
                .flatMap({ profileToBeSaved -> profileRepository.save(profileToBeSaved) })


        StepVerifier
                .create(savedProfile)
                .expectError(DuplicateKeyException.class)
                .verify()
    }

    def 'verify that call to fetch a profile by user id succeeds'() {
        given:
            LocalDateTime start = LocalDateTime.of(2011, 8, 22,0,0,0)
            LocalDateTime end = LocalDateTime.of(2015,5,17,0,0,0)
            Education education = Education.builder()
                    .current(false)
                    .degree("BS")
                    .fieldOfStudy("Computer Science")
                    .school("University of Georgia")
                    .description("i got a degree")
                    .from(start.toDate())
                    .to(end.toDate())
                    .build()

            Mono<Profile> savedProfile = template.save(MongoUser.builder()
                    .name("testy mctesterson")
                    .email("testy@test.com")
                    .password("test123")
                    .build(), "users")
                    .map({ mu ->
                        User.builder()
                                .id(mu.getId().toString())
                                .name(mu.getName())
                                .password(mu.getPassword())
                                .email(mu.getEmail())
                                .build()
                    })
                    .map({ user ->
                        Profile.builder()
                                .userId(user.getId())
                                .company("ACME")
                                .status("Software Developer")
                                .website("www.acme.com")
                                .skills(List.of("Java", "Javascript", "Scala", "HTML", "CSS",
                                        "Python"))
                                .location("Atlanta, GA")
                                .bio("I do code")
                                .gitHubUserName("testymac")
                                .education(List.of(education))
                                .build()
                    })
                    .flatMap({ p -> profileRepository.save(p) })
                    .flatMap({ pp -> profileRepository.findByUserId(pp.getUserId()) })

            StepVerifier
                    .create(savedProfile)
                    .expectNextCount(1)
                    .expectComplete()
                    .verify()

    }
    def 'verify that call to fetch a profile by invalid user id succeeds with empty value'() {
        given:
            Mono<Profile> noProfile = profileRepository.findByUserId(ObjectId.get().toString())

        StepVerifier
            .create(noProfile)
            .expectNextCount(0)
            .expectComplete()
            .verify()

    }

    def 'verify created Profile is deleted'() {

        given:

            LocalDateTime start = LocalDateTime.of(2011, 8, 22,0,0,0)
            LocalDateTime end = LocalDateTime.of(2015,5,17,0,0,0)
            Education education = Education.builder()
                    .current(false)
                    .degree("BS")
                    .fieldOfStudy("Computer Science")
                    .school("University of Georgia")
                    .description("i got a degree")
                    .from(start.toDate())
                    .to(end.toDate())
                    .build()

            Mono<Void> deletedProfile = template.save(MongoUser.builder()
                    .name("testy mctesterson")
                    .email("testy@test.com")
                    .password("test123")
                    .build(), "users")
                    .flatMap({ u ->
                        return Mono.just(Profile.builder()
                                .userId(u.getId().toString())
                                .company("ACME")
                                .status("Software Developer")
                                .website("www.acme.com")
                                .skills(List.of("Java", "Javascript", "Scala", "HTML", "CSS",
                                        "Python"))
                                .location("Atlanta, GA")
                                .bio("I do code")
                                .gitHubUserName("testymac")
                                .education(List.of(education))
                                .build())
                    })
                    .flatMap({ profile -> profileRepository.save(profile) })
                    .flatMap({ p -> profileRepository.deleteByUserId(p.getUserId()) })

        StepVerifier
                .create(deletedProfile)
                .verifyComplete()

    }

}

