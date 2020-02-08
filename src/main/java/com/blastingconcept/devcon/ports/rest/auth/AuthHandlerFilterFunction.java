package com.blastingconcept.devcon.ports.rest.auth;

import com.blastingconcept.devcon.ports.rest.AppResponseErrors;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.HandlerFilterFunction;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

import static org.springframework.http.HttpStatus.FORBIDDEN;


public class AuthHandlerFilterFunction implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private Key signingKey;

    public AuthHandlerFilterFunction(Key signingKey) {
        this.signingKey = signingKey;
    }

    @Override
    public Mono<ServerResponse> filter(ServerRequest request, HandlerFunction<ServerResponse> next) {

        if( request.exchange().getRequest().getHeaders().containsKey("x-auth-token")) {

            String token = request.exchange().getRequest().getHeaders().containsKey("x-auth-token") ?
                   request.exchange().getRequest().getHeaders().get("x-auth-token").get(0) : "";

            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(signingKey)
                        .parseClaimsJws(token).getBody();
                request.exchange().getAttributes().putIfAbsent("user", claims.get("user"));
            } catch (Exception e) {
                return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue(new AppResponseErrors(List.of(e.getMessage())));
            }

        } else {
            ServerHttpResponse response = request.exchange().getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return ServerResponse.status(FORBIDDEN).build();
        }

        return next.handle(request);

    }
}
