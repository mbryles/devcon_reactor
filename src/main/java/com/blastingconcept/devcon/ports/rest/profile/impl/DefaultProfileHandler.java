package com.blastingconcept.devcon.ports.rest.profile.impl;

import com.blastingconcept.devcon.domain.profile.*;
import com.blastingconcept.devcon.ports.rest.AbstractValidationHandler;
import com.blastingconcept.devcon.ports.rest.AppResponseErrors;
import com.blastingconcept.devcon.ports.rest.profile.*;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class DefaultProfileHandler extends AbstractValidationHandler implements ProfileHandler {

    private ProfileRepository profileRepository;

    protected DefaultProfileHandler(Validator validator, ProfileRepository profileRepository) {
        super(validator);
        this.profileRepository = profileRepository;
    }


    @Override
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(UpsertProfileDTO.class)
                .flatMap(body -> {
                    Errors errors = validateBody(body);

                    if (errors == null || errors.getAllErrors()
                            .isEmpty()) {
                        return this.profileRepository.save(Profile.builder()
                                    .userId(this.extractUserId(request))
                                    .company(body.getCompany())
                                    .status(body.getStatus())
                                    .website(body.getWebsite())
                                    .skills(Arrays.asList(body.getSkills().split(",")))
                                    .location(body.getLocation())
                                    .bio(body.getBio())
                                    .gitHubUserName(body.getGitHubUserName())
                                    .experience(this.mapToExperience(body.getExperience()))
                                    .education(this.mapToEducation(body.getEducation()))
                                    .social(this.mapToSocial(body.getSocial()))
                                    .build()
                                )

                                .flatMap(s -> ServerResponse.ok().bodyValue(
                                        UpsertProfileDTO.builder()
                                            .bio(body.getBio())
                                            .company(body.getCompany())
                                            .education(body.getEducation())
                                            .experience(body.getExperience())
                                            .gitHubUserName(body.getGitHubUserName())
                                            .location(body.getLocation())
                                            .skills(body.getSkills())
                                            .social(body.getSocial())
                                            .status(body.getStatus())
                                            .website(body.getWebsite())
                                            .build()
                                )

                                .onErrorResume(DuplicateKeyException.class,
                                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                .bodyValue( AppResponseErrors.builder()
                                                        .errors(List.of("Profile already exists for user " + s.getUserId())).build())
                                ));
                    } else {
                        return onValidationErrors(errors);
                    }
                });

    }

    @Override
    public Mono<ServerResponse> me(ServerRequest request) {
        return this.profileRepository.findByUserId(this.extractUserId(request))
                .flatMap(myProfile -> ok().bodyValue(UpsertProfileDTO.builder()
                        .website(myProfile.getWebsite())
                        .status(myProfile.getStatus())
                        .skills(String.join(",", myProfile.getSkills()))
                        .location(myProfile.getLocation())
                        .gitHubUserName(myProfile.getGitHubUserName())
                        .company(myProfile.getCompany())
                        .bio(myProfile.getBio())
                        .social(this.mapFromSocial(myProfile.getSocial()))
                        .experience(this.mapFromExperience(myProfile.getExperience()))
                        .education(this.mapFromEducation(myProfile.getEducation()))
                        .build()
                ))
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    @Override
    public Mono<ServerResponse> allProfiles(ServerRequest request) {

        Flux<UpsertProfileDTO> profileFlux = this.profileRepository.findAll()
                .map(myProfile -> UpsertProfileDTO.builder()
                        .website(myProfile.getWebsite())
                        .status(myProfile.getStatus())
                        .skills(String.join(",", myProfile.getSkills()))
                        .location(myProfile.getLocation())
                        .gitHubUserName(myProfile.getGitHubUserName())
                        .company(myProfile.getCompany())
                        .bio(myProfile.getBio())
                        .social(this.mapFromSocial(myProfile.getSocial()))
                        .experience(this.mapFromExperience(myProfile.getExperience()))
                        .education(this.mapFromEducation(myProfile.getEducation()))
                        .build()
                );
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileFlux, Profile.class)
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    private List<Experience> mapToExperience(List<ExperienceDTO> experienceDTOs) {
        return experienceDTOs == null ? Collections.emptyList() : experienceDTOs.stream()
                .filter(Objects::nonNull)
                .map( e ->  Experience.builder()
                        .company(e.getCompany())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .from(e.getFrom())
                        .to(e.getTo())
                        .location(e.getLocation())
                        .title(e.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ExperienceDTO> mapFromExperience(List<Experience> experiences) {
        return experiences == null ? Collections.emptyList() : experiences.stream()
                .filter(Objects::nonNull)
                .map( e ->  ExperienceDTO.builder()
                        .company(e.getCompany())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .from(e.getFrom())
                        .to(e.getTo())
                        .location(e.getLocation())
                        .title(e.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Education> mapToEducation(List<EducationDTO> educationDTOs) {
        return educationDTOs == null ? Collections.emptyList() : educationDTOs.stream()
                .filter(Objects::nonNull)
                .map( e -> Education.builder()
                        .degree(e.getDegree())
                        .school(e.getSchool())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .fieldOfStudy(e.getFieldOfStudy())
                        .from(e.getFrom())
                        .to(e.getTo())
                        .build()
                )
                .collect(Collectors.toList());

    }

    private List<EducationDTO> mapFromEducation(List<Education> education) {
        return education == null ? Collections.emptyList() : education.stream()
                .filter(Objects::nonNull)
                .map( e -> EducationDTO.builder()
                        .degree(e.getDegree())
                        .school(e.getSchool())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .fieldOfStudy(e.getFieldOfStudy())
                        .from(e.getFrom())
                        .to(e.getTo())
                        .build()
                )
                .collect(Collectors.toList());

    }

    private Social mapToSocial(SocialDTO social) {
        return social == null ? null : Social.builder()
                .facebook(social.getFacebook())
                .instagram(social.getInstagram())
                .linkedin(social.getLinkedin())
                .twitter(social.getTwitter())
                .youtube(social.getYoutube())
                .build();
    }

    private SocialDTO mapFromSocial(Social social) {
        return social == null ? null : SocialDTO.builder()
                .facebook(social.getFacebook())
                .instagram(social.getInstagram())
                .linkedin(social.getLinkedin())
                .twitter(social.getTwitter())
                .youtube(social.getYoutube())
                .build();
    }

    private String extractUserId(ServerRequest request) {

        LinkedHashMap<String,Object> attributeMap = (LinkedHashMap<String, Object>) request.attributes().get("user");
        return (String) attributeMap.get("id");

    }
}
