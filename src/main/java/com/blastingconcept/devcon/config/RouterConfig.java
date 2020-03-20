package com.blastingconcept.devcon.config;

import com.blastingconcept.devcon.ports.rest.auth.AuthenticationHandler;
import com.blastingconcept.devcon.ports.rest.auth.AuthHandlerFilterFunction;
import com.blastingconcept.devcon.ports.rest.post.impl.DefaultPostHandler;
import com.blastingconcept.devcon.ports.rest.profile.ProfileHandler;
import com.blastingconcept.devcon.ports.rest.user.impl.DefaultUserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.security.Key;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterConfig {

    private final Key signingKey;

    public RouterConfig(Key signingKey) {
        this.signingKey = signingKey;
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(DefaultUserHandler userHandler) {

        return route(GET("/api/users/{id}"), userHandler::getUserById)
                .filter(new AuthHandlerFilterFunction(signingKey));
    }

    @Bean
    public RouterFunction<ServerResponse> postRoutes(DefaultPostHandler postHandler) {
        return route(POST("/api/posts").and(contentType(APPLICATION_JSON)), postHandler::create)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/posts"), postHandler::allPosts)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/posts/{postId}"), postHandler::postById)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(DELETE("/api/posts/{postId}"), postHandler::deleteById)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(PUT("/api/posts/{postId}/like"), postHandler::like)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(PUT("/api/posts/{postId}/unlike"), postHandler::unlike)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(POST("/api/posts/{postId}/comments").and(contentType(APPLICATION_JSON)), postHandler::addComment)
                .filter((new AuthHandlerFilterFunction(signingKey)))
                .andRoute(DELETE("/api/posts/{postId}/comments/{commentId}"), postHandler::deleteComment)
                .filter((new AuthHandlerFilterFunction(signingKey)));


    }

    @Bean
    public RouterFunction<ServerResponse> authRoutes(AuthenticationHandler authenticationHandler) {

        return route(GET("/api/auth"), authenticationHandler::getAuthenticatedUser)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(POST("/api/users").and(contentType(APPLICATION_JSON)), authenticationHandler::register)
                .andRoute(POST("/api/auth").and(contentType(APPLICATION_JSON)), authenticationHandler::login);


    }

    @Bean
    public RouterFunction<ServerResponse> profileRoutes(ProfileHandler profileHandler) {
        return route(POST("/api/profile").and(contentType(APPLICATION_JSON)), profileHandler::create)
                        .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/profile/me"), profileHandler::me)
                        .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/profile"), profileHandler::allProfiles)
                        .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/profile/user/{userId}"), profileHandler::profileByUserId)
                .andRoute(PUT("/api/profile/experience").and(contentType(APPLICATION_JSON)),
                        profileHandler::addExperience)
                        .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(PUT("/api/profile/education").and(contentType(APPLICATION_JSON)),
                        profileHandler::addEducation)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(DELETE("/api/profile/experience/{experienceId}"), profileHandler::deleteExperience)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(DELETE("/api/profile/education/{educationId}"), profileHandler::deleteEducation)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/profile/github/{userId}"), profileHandler::githubRepos);
    }
}
