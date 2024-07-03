package com.testpayments.individuals_api.service;

import com.testpayments.individuals_api.dto.UserLoginRequest;
import com.testpayments.individuals_api.dto.UserRegistrationRequest;
import com.testpayments.individuals_api.dto.UserAuthenticationResponse;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public interface KeycloakUserService {

    Mono<UserAuthenticationResponse> createUser(UserRegistrationRequest userRegistrationRequest);

    Mono<UserAuthenticationResponse> login(UserLoginRequest userLoginRequest);

    Mono<UserRepresentation> getUserById(String userId);
}
