package com.blastingconcept.devcon.ports.rest.profile.impl;

import com.blastingconcept.devcon.domain.auth.AuthenticationService;
import com.blastingconcept.devcon.domain.profile.*;
import com.blastingconcept.devcon.domain.user.UserRepository;
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

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class DefaultProfileHandler extends AbstractValidationHandler implements ProfileHandler {

    private ProfileService profileService;
    private AuthenticationService authenticationService;
    private UserRepository userRepository;

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    protected DefaultProfileHandler(Validator validator, ProfileService profileService, UserRepository userRepository,
                                    AuthenticationService authenticationService) {
        super(validator);
        this.profileService = profileService;
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }


    @Override
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(UpsertProfileDTO.class)
                .flatMap(body -> {
                    Errors errors = validateBody(body);

                    if (errors == null || errors.getAllErrors()
                            .isEmpty()) {
                        return this.profileService.saveProfile(Profile.builder()
                                    .userId(this.extractUserAttribute(request, "id"))
                                    .company(body.getCompany())
                                    .status(body.getStatus())
                                    .website(body.getWebsite())
//                                    .skills(Arrays.asList(body.getSkills().split(",")))
                                        .skills(body.getSkills())
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
        return this.profileService.fetchProfileByUserId(this.extractUserAttribute(request, "id"))
                .flatMap(myProfile -> ok().bodyValue(UpsertProfileDTO.builder()
                        .website(myProfile.getWebsite())
                        .status(myProfile.getStatus())
//                        .skills(String.join(",", myProfile.getSkills()))
                        .skills(myProfile.getSkills())
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

        Flux<ProfileDTO> profileFlux = this.profileService.fetchAllProfiles()
                .flatMap(myProfile -> userRepository.findById(myProfile.getUserId())
                .map(user -> ProfileDTO.builder()
                        .user(UserDTO.builder()
                                .id(user.getId())
                                    .avatar(user.getAvatar())
                                    .name(user.getName())
                                    .build())
                        .website(myProfile.getWebsite())
                        .status(myProfile.getStatus())
                        .skills( myProfile.getSkills())
                        .location(myProfile.getLocation())
                        .gitHubUserName(myProfile.getGitHubUserName())
                        .company(myProfile.getCompany())
                        .bio(myProfile.getBio())
                        .social(this.mapFromSocial(myProfile.getSocial()))
                        .experience(this.mapFromExperience(myProfile.getExperience()))
                        .education(this.mapFromEducation(myProfile.getEducation()))
                        .build()));
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(profileFlux, ProfileDTO.class)
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    @Override
    public Mono<ServerResponse> profileByUserId(ServerRequest request) {

        Mono<ProfileDTO> profileDTOMono = this.profileService.fetchProfileByUserId(request.pathVariable("userId"))
                .flatMap(myProfile -> userRepository.findById(myProfile.getUserId())
                .map(user -> ProfileDTO.builder()
                        .user(UserDTO.builder()
                                .id(user.getId())
                                .avatar(user.getAvatar())
                                .name(user.getName())
                                .build())
                        .website(myProfile.getWebsite())
                        .status(myProfile.getStatus())
                        .skills( myProfile.getSkills())
                        .location(myProfile.getLocation())
                        .gitHubUserName(myProfile.getGitHubUserName())
                        .company(myProfile.getCompany())
                        .bio(myProfile.getBio())
                        .social(this.mapFromSocial(myProfile.getSocial()))
                        .experience(this.mapFromExperience(myProfile.getExperience()))
                        .education(this.mapFromEducation(myProfile.getEducation()))
                        .build()));

        return ServerResponse.ok()
                .body(profileDTOMono, ProfileDTO.class)
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    @Override
    public Mono<ServerResponse> addExperience(ServerRequest request) {
        return request.bodyToMono(ExperienceDTO.class)
                .flatMap(experienceDTO -> {
                    Errors errors = validateBody(experienceDTO);

                    if (errors == null || errors.getAllErrors()
                            .isEmpty()) {
                                return  this.profileService.addExperienceToProfile(Experience.builder()
                                            .title(experienceDTO.getTitle())
                                            .company(experienceDTO.getCompany())
                                            .current(experienceDTO.getCurrent())
                                            .description(experienceDTO.getDescription())
                                            .from(Date.from(LocalDate.parse(
                                                    experienceDTO.getFrom()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                                            .location(experienceDTO.getLocation())
                                            .to(experienceDTO.getCurrent() ? new Date() : Date.from(LocalDate.parse(
                                                    experienceDTO.getTo()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                                            .build(), this.extractUserAttribute(request, "id"))
                                        .flatMap(s -> ServerResponse.ok()
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .bodyValue(
                                                    UpsertProfileDTO.builder()
                                                        .bio(s.getBio())
                                                        .company(s.getCompany())
                                                        .education(this.mapFromEducation(s.getEducation()))
                                                        .experience(this.mapFromExperience(s.getExperience()))
                                                        .gitHubUserName(s.getGitHubUserName())
                                                        .location(s.getLocation())
//                                                        .skills(String.join("," ,s.getSkills()))
                                                            .skills(s.getSkills())
                                                        .social(this.mapFromSocial(s.getSocial()))
                                                        .status(s.getStatus())
                                                        .website(s.getWebsite())
                                                        .build()
                                                )
                                                .onErrorResume(Exception.class,
                                                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build())));

                    } else {
                        return onValidationErrors(errors);
                    }
                });

    }

    @Override
    public Mono<ServerResponse> deleteExperience(ServerRequest request) {

        String uid = this.extractUserAttribute(request, "id");

        return this.profileService.deleteExperienceFromProfile(request.pathVariable("experienceId"), uid)

                .flatMap(p -> ok().bodyValue(UpsertProfileDTO.builder()
                        .website(p.getWebsite())
                        .status(p.getStatus())
//                        .skills(String.join("," ,p.getSkills()))
                        .skills(p.getSkills())
                        .location(p.getLocation())
                        .gitHubUserName(p.getGitHubUserName())
                        .company(p.getCompany())
                        .bio(p.getBio())
                        .social(this.mapFromSocial(p.getSocial()))
                        .experience(this.mapFromExperience(p.getExperience()))
                        .education(this.mapFromEducation(p.getEducation()))
                        .build()

                ))
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));

    }

    @Override
    public Mono<ServerResponse> addEducation(ServerRequest request) {
        return request.bodyToMono(EducationDTO.class)
                .flatMap(educationDTO -> {
                    Errors errors = validateBody(educationDTO);

                    if (errors == null || errors.getAllErrors()
                            .isEmpty()) {

                        return  this.profileService.addEducationToProfile(Education.builder()
                                            .id(UUID.randomUUID().toString())
                                            .school(educationDTO.getSchool())
                                            .fieldOfStudy(educationDTO.getFieldOfStudy())
                                            .description(educationDTO.getDescription())
                                            .degree(educationDTO.getDegree())
                                            .current(educationDTO.getCurrent())
                                            .from(Date.from(LocalDate.parse(
                                                    educationDTO.getFrom()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                                            .to(Date.from(LocalDate.parse(
                                                    educationDTO.getTo()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                                            .build(), this.extractUserAttribute(request, "id"))
                                .flatMap(s -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(
                                                UpsertProfileDTO.builder()
                                                        .bio(s.getBio())
                                                        .company(s.getCompany())
                                                        .education(this.mapFromEducation(s.getEducation()))
                                                        .experience(this.mapFromExperience(s.getExperience()))
                                                        .gitHubUserName(s.getGitHubUserName())
                                                        .location(s.getLocation())
//                                                        .skills(String.join("," ,s.getSkills()))
                                                        .skills(s.getSkills())
                                                        .social(this.mapFromSocial(s.getSocial()))
                                                        .status(s.getStatus())
                                                        .website(s.getWebsite())
                                                        .build()
                                        )
                                        .onErrorResume(Exception.class,
                                                t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                                        .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build())));

                    } else {
                        return onValidationErrors(errors);
                    }
                });

    }

    @Override
    public Mono<ServerResponse> deleteEducation(ServerRequest request) {
        String uid = this.extractUserAttribute(request, "id");

        return this.profileService.deleteEducationFromProfile(request.pathVariable("educationId"), uid)
                .flatMap(p -> ok().build())
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    @Override
    public Mono<ServerResponse> githubRepos(ServerRequest request) {
        Mono<String>  repos = this.profileService.fetchGithubRepos(request.pathVariable("userId"));

        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(repos, String.class)
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));


    }

    @Override
    public Mono<ServerResponse> deleteAccount(ServerRequest request) {
        String uid = this.extractUserAttribute(request, "id");

        return this.authenticationService.deleteAccount(uid)
                .flatMap(p -> ok().build())
                .switchIfEmpty(notFound().build())
                .onErrorResume(Exception.class,
                        t -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .bodyValue( AppResponseErrors.builder().errors(List.of(t.getMessage())).build()));
    }

    private List<Experience> mapToExperience(List<ExperienceDTO> experienceDTOs) {
        return experienceDTOs == null ? Collections.emptyList() : experienceDTOs.stream()
                .filter(Objects::nonNull)
                .map( e ->  Experience.builder()
                        .id(e.getId())
                        .company(e.getCompany())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .from(Date.from(LocalDate.parse(
                                e.getFrom()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                        .to(Date.from(LocalDate.parse(
                                e.getTo()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                        .location(e.getLocation())
                        .title(e.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    private List<ExperienceDTO> mapFromExperience(List<Experience> experiences) {
        return experiences == null ? Collections.emptyList() : experiences.stream()
                .filter(Objects::nonNull)
                .map( e ->  ExperienceDTO.builder()
                        .id(e.getId())
                        .company(e.getCompany())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .from((e.getFrom().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()).format(formatter))
                        .to((e.getTo().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()).format(formatter))
                        .location(e.getLocation())
                        .title(e.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Education> mapToEducation(List<EducationDTO> educationDTOs) {
        return educationDTOs == null ? Collections.emptyList() : educationDTOs.stream()
                .filter(Objects::nonNull)
                .map( e -> Education.builder()
                        .id(e.getId())
                        .degree(e.getDegree())
                        .school(e.getSchool())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .fieldOfStudy(e.getFieldOfStudy())
                        .from(Date.from(LocalDate.parse(
                                e.getFrom()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                        .to(Date.from(LocalDate.parse(
                                e.getTo()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                        .build()
                )
                .collect(Collectors.toList());

    }

    private List<EducationDTO> mapFromEducation(List<Education> education) {
        return education == null ? Collections.emptyList() : education.stream()
                .filter(Objects::nonNull)
                .map( e -> EducationDTO.builder()
                        .id(e.getId())
                        .degree(e.getDegree())
                        .school(e.getSchool())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .fieldOfStudy(e.getFieldOfStudy())
                        .from((e.getFrom().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()).format(formatter))
                        .to((e.getTo().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()).format(formatter))
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

    private String extractUserAttribute(ServerRequest request, String key) {

        LinkedHashMap<String,Object> attributeMap = (LinkedHashMap<String, Object>) request.attributes().get("user");
        return (String) attributeMap.get(key);

    }
}
