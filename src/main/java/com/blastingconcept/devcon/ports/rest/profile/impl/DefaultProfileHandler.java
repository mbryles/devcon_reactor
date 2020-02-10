package com.blastingconcept.devcon.ports.rest.profile.impl;

import com.blastingconcept.devcon.domain.profile.ProfileRepository;
import com.blastingconcept.devcon.ports.rest.AbstractValidationHandler;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

@Component
public class DefaultProfileHandler extends AbstractValidationHandler implements com.blastingconcept.devcon.ports.rest.profile.ProfileHandler {

    private ProfileRepository profileRepository;

    protected DefaultProfileHandler(Validator validator, ProfileRepository profileRepository) {
        super(validator);
        this.profileRepository = profileRepository;
    }




}
