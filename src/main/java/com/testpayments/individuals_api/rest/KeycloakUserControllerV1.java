package com.testpayments.individuals_api.rest;

import com.testpayments.individuals_api.dto.UserLoginRequest;
import com.testpayments.individuals_api.dto.UserRegistrationRequest;
import com.testpayments.individuals_api.dto.UserAuthenticationResponse;
import com.testpayments.individuals_api.service.KeycloakUserService;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.security.Principal;

@RestController
@RequestMapping(KeycloakUserControllerV1.ROOT_URL)
@RequiredArgsConstructor
public class KeycloakUserControllerV1 {

    public static final String ROOT_URL = "/api/v1/users";

    private final KeycloakUserService keycloakUserService;

    @PostMapping("/register")
    public Mono<UserAuthenticationResponse> register(@RequestBody UserRegistrationRequest userRegistrationRequest) {
        return keycloakUserService.createUser(userRegistrationRequest);
    }

    @PostMapping("/login")
    public Mono<UserAuthenticationResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        return keycloakUserService.login(userLoginRequest);
    }

    @GetMapping("/user-info")
    public Mono<UserRepresentation> getInfo(Principal principal) {
        return keycloakUserService.getUserById(principal.getName());
    }
}
