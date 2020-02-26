package com.blastingconcept.devcon.ports.persistence.post

import com.blastingconcept.devcon.domain.post.Comment
import com.blastingconcept.devcon.domain.post.Post
import com.blastingconcept.devcon.domain.post.PostRepository

import com.blastingconcept.devcon.ports.persistence.user.MongoUser
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.IgnoreIf
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

@IgnoreIf( {System.getProperty("integrationTest") == null || System.getProperty("integrationTest").is(false) })
class MongoPostRepositorySpec extends Specification {

    @Subject
    PostRepository postRepository

    @Shared
    MongoClient reactiveMongoClient

    @Shared
    ReactiveMongoTemplate template

    def setup() {

        reactiveMongoClient =  MongoClients.create("mongodb://localhost:27017/")

        template =  new ReactiveMongoTemplate(reactiveMongoClient, "devcon_test")

        ((MappingMongoConverter)template.getConverter()).setTypeMapper(new DefaultMongoTypeMapper(null));

        postRepository = new ReactiveMongoPostRepository(template)

    }

    def cleanup() {

        template.findAllAndRemove(new Query(), MongoPost.class)
                .flatMap({ mongoPost ->
                    template.findAndRemove(new Query().addCriteria(Criteria.where("_id")
                            .is(mongoPost.getUserId())), MongoUser.class)
                }).blockLast()
    }

    def setupSpec() {
        println "Setup specification"
    }

    def cleanupSpec() {
        println "Clean up specification"
        template.dropCollection("users").block()
        template.dropCollection("posts").block()
    }


    def 'verify ProfileRepository is not null'() {
        expect:
            postRepository != null
    }

    def 'verify create Post'() {

        given:
            Mono<Post> post = template.save(MongoUser.builder()
                    .name("testy mctesterson")
                    .email("testy@test.com")
                    .password("test123")
                    .build(), "users")
                .flatMap({ user ->
                    Mono.just(Post.builder()
                            .name("this is a post")
                            .date(new Date())
                            .text("lorum ipsum stuff")
                            .userId(user.getId().toString())
                            .avatar("avatar")
                            .build())
                })
                    .flatMap({ postToSave -> postRepository.save(postToSave) })

        StepVerifier
                .create(post)
                .expectNextCount(1)
                .expectComplete()
                .verify()

    }

    def 'verify call to fetch all posts succeeds'(){
        given:

            Comment comment1 = Comment.builder()
                .id(UUID.randomUUID().toString())
                .name("here's a comment title")
                .text("lorum ipsum text")
                .userId("1231231231")
                .date(new Date())
                .build()

            Comment comment2 = Comment.builder()
                    .id(UUID.randomUUID().toString())
                    .name("here's a comment title 2")
                    .text("lorum ipsum text foo 2")
                    .userId("00012adfasdf3123123adfsfd1")
                    .date(new Date())
                    .build()

            List<Comment> comments = new ArrayList<>()
            comments.add(comment1)
            comments.add(comment2)

            Mono<Post> savedPost1 = template.save(MongoUser.builder()
                        .name("testy mctesterson")
                        .email("testy44@test.com")
                        .password("test123")
                        .build(), "users")
                    .map({ user ->
                    Post.builder()
                            .userId(user.getId().toString())
                            .avatar("avatar1")
                            .text("some text for post1")
                            .name("post 1")
                            .comments(comments)
                            .build()
                })
                .flatMap({ p -> postRepository.save(p) })

        StepVerifier
                .create(savedPost1)
                .expectNextCount(1)
                .expectComplete()
                .verify()


        Mono<Post> savedPost2 = template.save(MongoUser.builder()
                    .name("testy mctesterson beta")
                    .email("testalpha4@test.com")
                    .password("test123alpha")
                    .build(), "users")
                .map({ user ->
                    Post.builder()
                            .userId(user.getId().toString())
                            .avatar("avatar2")
                            .text("some text for post2")
                            .name("post 2")
                            .build()
                })
                .flatMap({ p -> postRepository.save(p) })


        StepVerifier
                .create(savedPost2)
                .expectNextCount(1)
                .expectComplete()
                .verify()

        Flux<Post> allPosts = postRepository.findAll()

        StepVerifier
                .create(allPosts)
                .expectNextCount(2)
                .expectComplete()
                .verify()
    }

    def 'verify that call to get a single post succeeds'() {
        given:

            Comment comment1 = Comment.builder()
                    .id(UUID.randomUUID().toString())
                    .name("here's a comment title")
                    .text("lorum ipsum text")
                    .userId("1231231231")
                    .date(new Date())
                    .build()

            Comment comment2 = Comment.builder()
                    .id(UUID.randomUUID().toString())
                    .name("here's a comment title 2")
                    .text("lorum ipsum text foo 2")
                    .userId("00012adfasdf3123123adfsfd1")
                    .date(new Date())
                    .build()

            List<Comment> comments = new ArrayList<>()
            comments.add(comment1)
            comments.add(comment2)

            Mono<Post> savedPost = template.save(MongoUser.builder()
                    .name("testy mctesterson")
                    .email("testy44@test.com")
                    .password("test123")
                    .build(), "users")
                    .map({ user ->
                        Post.builder()
                                .userId(user.getId().toString())
                                .avatar("avatar1")
                                .text("some text for post1")
                                .name("post 1")
                                .comments(comments)
                                .build()
                    })
                    .flatMap({ p -> postRepository.save(p) })
                    .flatMap({ pp -> postRepository.findById(pp.getId()) })

        StepVerifier
                .create(savedPost)
                .expectNextCount(1)
                .expectComplete()
                .verify()
    }

    def 'verify that call to fetch a post by invalid id succeeds with empty value'() {
        given:
            Mono<Post> noPost = postRepository.findById(ObjectId.get().toString())

        StepVerifier
                .create(noPost)
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    def 'verify that created post is deleted'() {

        given:

            Comment comment1 = Comment.builder()
                    .id(UUID.randomUUID().toString())
                    .name("here's a comment title")
                    .text("lorum ipsum text")
                    .userId("1231231231")
                    .date(new Date())
                    .build()

            Comment comment2 = Comment.builder()
                    .id(UUID.randomUUID().toString())
                    .name("here's a comment title 2")
                    .text("lorum ipsum text foo 2")
                    .userId("00012adfasdf3123123adfsfd1")
                    .date(new Date())
                    .build()

            List<Comment> comments = new ArrayList<>()
            comments.add(comment1)
            comments.add(comment2)

            Mono<Post> savedPost = template.save(MongoUser.builder()
                    .name("testy mctesterson")
                    .email("testy44@test.com")
                    .password("test123")
                    .build(), "users")
                    .map({ user ->
                        Post.builder()
                                .userId(user.getId().toString())
                                .avatar("avatar1")
                                .text("some text for post1")
                                .name("post 1")
                                .comments(comments)
                                .build()
                    })
                    .flatMap({ p -> postRepository.save(p) })
                    .flatMap({ pp -> postRepository.deleteById(pp.getId()) })

        StepVerifier
                .create(savedPost)
                .expectNextCount(0)
                .expectComplete()
                .verify()

    }
}
