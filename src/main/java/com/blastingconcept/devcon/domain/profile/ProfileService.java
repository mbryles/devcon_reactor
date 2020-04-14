package com.blastingconcept.devcon.domain.profile;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProfileService {

    Mono<Profile> saveProfile(Profile profile);
    Mono<Profile> fetchProfileByUserId(String userId);
    Flux<Profile> fetchAllProfiles();
    Mono<Profile> addExperienceToProfile(Experience experience, String userId);
    Mono<Profile> addEducationToProfile(Education education, String userId);
    Mono<Profile> deleteExperienceFromProfile(String experienceId, String userId);
    Mono<Profile> deleteEducationFromProfile(String educationId, String userId);
    Mono<String> fetchGithubRepos(String userName);
    Mono<Void> deleteByUserId(String userId);
}
