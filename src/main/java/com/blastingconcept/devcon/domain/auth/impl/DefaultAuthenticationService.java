package com.blastingconcept.devcon.domain.auth.impl;

import com.blastingconcept.devcon.domain.auth.AuthenticationService;
import com.blastingconcept.devcon.domain.auth.InvalidCredentialsException;
import com.blastingconcept.devcon.domain.auth.UserLogin;
import com.blastingconcept.devcon.domain.post.PostService;
import com.blastingconcept.devcon.domain.profile.ProfileService;
import com.blastingconcept.devcon.domain.user.User;
import com.blastingconcept.devcon.domain.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Date;

@Service
public class DefaultAuthenticationService implements AuthenticationService {

    private Key signingKey;
    private UserRepository userRepository;
    private ProfileService profileService;
    private PostService postService;
    private PasswordEncoder passwordEncoder;

    public DefaultAuthenticationService(Key signingKey, UserRepository userRepository, PasswordEncoder passwordEncoder,
                                        ProfileService profileService, PostService postService) {
        this.signingKey = signingKey;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.postService = postService;
    }

    @Override
    public Mono<String> register(User user) {

        return this.userRepository.save(user)
                .map(u -> createJWT(u, "DEVCON APP", u.getName(), 8000));

    }

    @Override
    public Mono<User> getAuthenticatedUser(String token) {

        Claims claims = this.decodeJWT(token);

        return this.userRepository.findById(claims.getId());
    }

    @Override
    public Mono<String> login(UserLogin userLogin) {


        return this.userRepository.findByEmail(userLogin.getEmail())
                .filter(user -> passwordEncoder.matches(userLogin.getPassword(), user.getPassword()))
                .map( user ->
                    createJWT(user, "DEVCON_APP", user.getName(), 3600000)
                )
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Invalid Credentials")));

    }

    @Override
    public Mono<Void> deleteAccount(String userId) {

        return Mono.fromSupplier(
                () -> {
                    this.postService.deleteAllUserPosts(userId)
                            .and(this.profileService.deleteByUserId(userId))
                            .and(this.userRepository.findById(userId)
                                .flatMap(user -> userRepository.delete(user))
                            )
                            .subscribe();
                    return null;
                }
        );
    }

    private String createJWT(User user, String issuer, String subject, long millis) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder()
                .setId(user.getId())
                .claim("user", user)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(signingKey);

        if (millis > 0) {
            long expMillis = nowMillis + millis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    private Claims decodeJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(signingKey)
                .parseClaimsJws(jwt).getBody();
    }

}
