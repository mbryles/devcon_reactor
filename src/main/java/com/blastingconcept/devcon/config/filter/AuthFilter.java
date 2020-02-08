package com.blastingconcept.devcon.config.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
public class AuthFilter implements WebFilter {

    private Key signingKey;

    public AuthFilter(Key signingKey) {
        this.signingKey = signingKey;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {



//        if(exchange.getRequest().getHeaders().containsKey("x-auth-token")) {
//
//            String token = exchange.getRequest().getHeaders().containsKey("x-auth-token") ?
//                    exchange.getRequest().getHeaders().get("x-auth-token").get(0) : "";
//
//            Claims claims = Jwts.parser()
//                    .setSigningKey(signingKey)
//                    .parseClaimsJws(token).getBody();
//
//            exchange.getAttributes().putIfAbsent("user", claims.get("user"));
//
//        } else {
//            ServerHttpResponse response = exchange.getResponse();
//            response.setStatusCode(HttpStatus.FORBIDDEN);
//            return response.setComplete();
//        }


//        exchange.getResponse()
//                .getHeaders()
//                .add("web-filter", "web-filter-test");


        return chain.filter(exchange);
    }
}
