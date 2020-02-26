package com.blastingconcept.devcon.config;

import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.HashMap;

@Configuration
public class AppConfig {

    @Value("${jwt.secret}")
    String secretKey;

    @Value("${github.clientId}")
    String githubClientId;

    @Value("$github.clientSecret")
    String  getGithubClientSecret;

    @Bean
    public Key signingKey() {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    }

    @Bean
    @Primary
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebClient webClient() {

        HashMap<String,Object> map = new HashMap<>();
        map.put("client_id", githubClientId);
        map.put("client_secret", getGithubClientSecret);

        return WebClient
                .builder()
                .baseUrl("https://api.github.com")
                .defaultHeader(HttpHeaders.USER_AGENT, "java")
                .defaultUriVariables(map)
                .build();
    }
}
