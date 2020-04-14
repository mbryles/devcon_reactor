package com.blastingconcept.devcon.domain.profile.impl;

import com.blastingconcept.devcon.domain.profile.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DefaultProfileService implements ProfileService {


   private ProfileRepository profileRepository;
   private WebClient webClient;

   public DefaultProfileService(ProfileRepository profileRepository, WebClient webClient) {
       this.profileRepository = profileRepository;
       this.webClient = webClient;
   }

    @Override
    public Mono<Profile> saveProfile(Profile profile) {
        return this.profileRepository.save(profile);
    }

    @Override
    public Mono<Profile> fetchProfileByUserId(String userId) {
        return this.profileRepository.findByUserId(userId);
    }

    @Override
    public Flux<Profile> fetchAllProfiles() {
        return this.profileRepository.findAll();
    }

    @Override
    public Mono<Profile> addExperienceToProfile(Experience experience, String userId) {
       return this.profileRepository.findByUserId(userId)
                .map(profile -> {

                    List<Experience> experiences = profile.getExperience();
                    experiences.add(experience.toBuilder()
                            .id(UUID.randomUUID().toString())
                            .build());


                    return profile.toBuilder()
                            .experience(experiences)
                            .build();
                })
                .flatMap(profileToSave -> this.profileRepository.save(profileToSave));
    }

    @Override
    public Mono<Profile> addEducationToProfile(Education education, String userId) {
        return this.profileRepository.findByUserId(userId)
                .map(profile -> {

                    List<Education> educations = profile.getEducation();
                    educations.add(education.toBuilder()
                            .id(UUID.randomUUID().toString())
                            .build());

                    return profile.toBuilder()
                            .education(educations)
                            .build();
                })
                .flatMap(profileToSave -> this.profileRepository.save(profileToSave));
    }

    @Override
    public Mono<Profile> deleteExperienceFromProfile(String experienceId, String userId) {

        return this.profileRepository.findByUserId(userId)
                .flatMap(myProfile -> this.profileRepository.save(myProfile.toBuilder()
                        .userId(userId)
                        .experience(this.unshiftExperiencesById(myProfile.getExperience(), experienceId))
                        .build()
                ));

    }

    @Override
    public Mono<Profile> deleteEducationFromProfile(String educationId, String userId) {
        return this.profileRepository.findByUserId(userId)
                .flatMap(myProfile -> this.profileRepository.save(myProfile.toBuilder()
                        .userId(userId)
                        .education(this.unshiftEducationsById(myProfile.getEducation(), educationId))
                        .build()
                ));
    }

    @Override
    public Mono<String> fetchGithubRepos(String userName) {

       return webClient
               .get()
               .uri("/users/" + userName + "/repos")
               .retrieve()
               .bodyToMono(String.class);
    }

    @Override
    public Mono<Void> deleteByUserId(String userId) {
        return this.profileRepository.deleteByUserId(userId);
    }

    private List<Experience> unshiftExperiencesById(List<Experience> experiences, String id) {

        return experiences.stream()
                .filter(p -> ! p.getId().equals(id))
                .collect(Collectors.toList());

    }

    private List<Education> unshiftEducationsById(List<Education> educations, String id) {

        return educations.stream()
                .filter(p -> ! p.getId().equals(id))
                .collect(Collectors.toList());

    }
}
