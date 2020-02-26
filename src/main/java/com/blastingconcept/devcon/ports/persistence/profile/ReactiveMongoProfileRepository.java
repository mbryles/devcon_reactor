package com.blastingconcept.devcon.ports.persistence.profile;

import com.blastingconcept.devcon.domain.profile.*;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ReactiveMongoProfileRepository implements ProfileRepository {

    private ReactiveMongoTemplate reactiveMongoTemplate;

    public ReactiveMongoProfileRepository(ReactiveMongoTemplate reactiveMongoTemplate) {
        this.reactiveMongoTemplate = reactiveMongoTemplate;
    }

    @Override
    public Mono<Profile> save(Profile profile) {

        List<MongoEducation> mongoEducations = this.mapFromEducation(profile.getEducation());

        List<MongoExperience> mongoExperiences = this.mapFromExperience(profile.getExperience());

        MongoSocial mongoSocial = this.mapFromSocial(profile.getSocial());

        return reactiveMongoTemplate.findOne(Query.query(Criteria.where("userId").is(profile.getUserId())), MongoProfile.class)
                .flatMap(updateMongoProfile -> reactiveMongoTemplate.save(MongoProfile.builder()
                        .id(updateMongoProfile.getId())
                        .company(profile.getCompany())
                        .bio(profile.getBio())
                        .gitHubUserName(profile.getGitHubUserName())
                        .status(profile.getStatus())
                        .website(profile.getWebsite())
                        .location(profile.getLocation())
                        .skills(profile.getSkills())
                        .date(new Date())
                        .education(mongoEducations)
                        .experience(mongoExperiences)
                        .social(mongoSocial)
                        .userId(profile.getUserId())
                        .build(), "profiles"))
                .switchIfEmpty(Mono.just(MongoProfile.builder()
                        .company(profile.getCompany())
                        .bio(profile.getBio())
                        .gitHubUserName(profile.getGitHubUserName())
                        .status(profile.getStatus())
                        .website(profile.getWebsite())
                        .location(profile.getLocation())
                        .skills(profile.getSkills())
                        .date(new Date())
                        .education(mongoEducations)
                        .experience(mongoExperiences)
                        .social(mongoSocial)
                        .userId(profile.getUserId())
                        .build()))
                .flatMap( profileToSave -> reactiveMongoTemplate.save(profileToSave, "profiles"))
                .map(p -> profile);

    }

    @Override
    public Mono<Profile> findByUserId(String id) {
        return reactiveMongoTemplate.findOne(new Query().addCriteria(Criteria.where("userId").is(id)),MongoProfile.class, "profiles")
                    .map(mongoProfile ->
                            Profile.builder()
                                    .bio(mongoProfile.getBio())
                                    .company(mongoProfile.getCompany())
                                    .date(mongoProfile.getDate())
                                    .education(this.mapToEducation(mongoProfile.getEducation()))
                                    .experience(this.mapToExperience(mongoProfile.getExperience()))
                                    .gitHubUserName(mongoProfile.getGitHubUserName())
                                    .location(mongoProfile.getLocation())
                                    .skills(mongoProfile.getSkills())
                                    .status(mongoProfile.getStatus())
                                    .website(mongoProfile.getWebsite())
                                    .social(this.mapFromSocial(mongoProfile.getSocial()))
                                    .userId(mongoProfile.getUserId())
                                    .build()

                    );
    }

    @Override
    public Mono<Void> deleteByUserId(String id) {
        return reactiveMongoTemplate.findAndRemove(new Query().addCriteria(Criteria.where("userId").is(id)), MongoProfile.class)
                .then();
    }

    @Override
    public Flux<Profile> findAll() {
        return reactiveMongoTemplate.findAll(MongoProfile.class, "profiles")
                .map(mongoProfile ->  Profile.builder()
                        .bio(mongoProfile.getBio())
                        .company(mongoProfile.getCompany())
                        .date(mongoProfile.getDate())
                        .education(this.mapToEducation(mongoProfile.getEducation()))
                        .experience(this.mapToExperience(mongoProfile.getExperience()))
                        .gitHubUserName(mongoProfile.getGitHubUserName())
                        .location(mongoProfile.getLocation())
                        .skills(mongoProfile.getSkills())
                        .status(mongoProfile.getStatus())
                        .website(mongoProfile.getWebsite())
                        .social(this.mapFromSocial(mongoProfile.getSocial()))
                        .userId(mongoProfile.getUserId())
                        .build()
                );
    }

    private List<MongoEducation> mapFromEducation(List<Education> education) {
        return education == null ? Collections.emptyList() : education.stream()
                .map( e -> MongoEducation.builder()
                        .id(e.getId())
                        .degree(e.getDegree())
                        .school(e.getSchool())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .fieldOfStudy(e.getFieldOfStudy())
                        .from(e.getFrom())
                        .to(e.getTo())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Education> mapToEducation(List<MongoEducation> education) {

        return education == null ? Collections.emptyList() : education.stream()
                .map( e -> Education.builder()
                        .id(e.getId())
                        .degree(e.getDegree())
                        .school(e.getSchool())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .fieldOfStudy(e.getFieldOfStudy())
                        .from(e.getFrom())
                        .to(e.getTo())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MongoExperience> mapFromExperience(List<Experience> experiences) {
        return experiences == null ? Collections.emptyList() : experiences.stream()
                .filter(Objects::nonNull)
                .map(e -> MongoExperience.builder()
                        .id(e.getId())
                        .company(e.getCompany())
                        .current(e.getCurrent())
                        .description(e.getDescription())
                        .location(e.getLocation())
                        .from(e.getFrom())
                        .title(e.getTitle())
                        .to(e.getTo())
                        .build())
                .collect(Collectors.toList());
    }

    private List<Experience> mapToExperience(List<MongoExperience> experiences) {
        return experiences == null ? Collections.emptyList() : experiences.stream()
                .map(ex -> Experience.builder()
                        .id(ex.getId())
                        .company(ex.getCompany())
                        .current(ex.getCurrent())
                        .description(ex.getDescription())
                        .from(ex.getFrom())
                        .to(ex.getTo())
                        .location(ex.getLocation())
                        .title(ex.getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    private MongoSocial mapFromSocial(Social social) {
        return social == null ? null : MongoSocial.builder()
                .facebook(social.getFacebook())
                .instagram(social.getInstagram())
                .linkedin(social.getLinkedin())
                .twitter(social.getTwitter())
                .youtube(social.getYoutube())
                .build();
    }

    private Social mapFromSocial(MongoSocial social) {
        return social == null ? null : Social.builder()
                .facebook(social.getFacebook())
                .instagram(social.getInstagram())
                .linkedin(social.getLinkedin())
                .twitter(social.getTwitter())
                .youtube(social.getYoutube())
                .build();
    }
}
