package com.blastingconcept.devcon.config;

import com.blastingconcept.devcon.ports.rest.auth.AuthenticationHandler;
import com.blastingconcept.devcon.ports.rest.auth.AuthHandlerFilterFunction;
import com.blastingconcept.devcon.ports.rest.post.PostHandler;
import com.blastingconcept.devcon.ports.rest.profile.ProfileHandler;
import com.blastingconcept.devcon.ports.rest.user.impl.DefaultUserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.security.Key;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterConfig {

    private final Key signingKey;

    public RouterConfig(Key signingKey) {
        this.signingKey = signingKey;
    }

    @Bean
    public RouterFunction<ServerResponse> userRoutes(DefaultUserHandler userHandler) {

        return RouterFunctions
                .route(GET("/api/users/{id}"), userHandler::getUserById)
                .filter(new AuthHandlerFilterFunction(signingKey));
    }

    @Bean
    public RouterFunction<ServerResponse> postRoutes(PostHandler postHandler) {
        return RouterFunctions
                .route(POST("/posts").and(contentType(APPLICATION_JSON)), postHandler::createPost);
    }

    @Bean
    public RouterFunction<ServerResponse> authRoutes(AuthenticationHandler authenticationHandler) {

        return RouterFunctions
                .route(GET("/api/auth"), authenticationHandler::getAuthenticatedUser)
                .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(POST("/api/users").and(contentType(APPLICATION_JSON)), authenticationHandler::register)
                .andRoute(POST("/api/auth").and(contentType(APPLICATION_JSON)), authenticationHandler::login);


    }

    @Bean
    public RouterFunction<ServerResponse> profileRoutes(ProfileHandler profileHandler) {
        return RouterFunctions
                .route(POST("/api/profile").and(contentType(APPLICATION_JSON)), profileHandler::create)
                        .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/profile/me"), profileHandler::me)
                        .filter(new AuthHandlerFilterFunction(signingKey))
                .andRoute(GET("/api/profile"), profileHandler::allProfiles)
                        .filter(new AuthHandlerFilterFunction(signingKey));
    }
}
