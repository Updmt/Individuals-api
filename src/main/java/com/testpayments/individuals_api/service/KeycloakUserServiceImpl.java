package com.testpayments.individuals_api.service;

import com.testpayments.individuals_api.config.KeycloakConfig;
import com.testpayments.individuals_api.dto.UserLoginRequest;
import com.testpayments.individuals_api.dto.UserRegistrationRequest;
import com.testpayments.individuals_api.dto.UserAuthenticationResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class KeycloakUserServiceImpl implements KeycloakUserService {

    @Value("${keycloak.realm}")
    private String realm;

    private final KeycloakConfig keycloakConfig;

    //Mono.fromCallable все, что внутри - выполнится лениво, после подписки, тк дорогостоящая операция.
    //Schedulers.boundedElastic()) выполняет блок кода в отдельном потоке, чтоб не блокировать все, что внутри
    @Override
    public Mono<UserAuthenticationResponse> createUser(UserRegistrationRequest request) {
        return checkIfPasswordCorrect(request)
                .then(Mono.fromCallable(() -> {
                    UserRepresentation user = toUserRepresentation(request);
                    RealmResource realmResource = keycloakConfig.getKeycloakInstanceWithDefault().realm(realm);
                    UsersResource usersResource = realmResource.users();

                    Response response = usersResource.create(user);

                    AccessTokenResponse token = keycloakConfig.getKeycloakInstance(request.getUsername(), request.getPassword()).tokenManager().getAccessToken();

                    if (response.getStatus() == Response.Status.CREATED.getStatusCode()) {
                        return toUserAuthenticationResponse(token);
                    } else {
                        throw new RuntimeException("Failed to create user: Status code " + response.getStatus());
                    }
                }).subscribeOn(Schedulers.boundedElastic()));
    }

    @Override
    public Mono<UserAuthenticationResponse> login(UserLoginRequest request) {
        AccessTokenResponse token = keycloakConfig.getKeycloakInstance(request.getUsername(), request.getPassword()).tokenManager().getAccessToken();
        return Mono.just(toUserAuthenticationResponse(token));
    }

    @Override
    public Mono<UserRepresentation> getUserById(String userId) {
        RealmResource realmResource = keycloakConfig.getKeycloakInstanceWithDefault().realm(realm);
        UsersResource usersResource = realmResource.users();
        return Mono.just(usersResource.get(userId).toRepresentation());
    }

    private Mono<Void> checkIfPasswordCorrect(UserRegistrationRequest userRegistrationRequest) {
        if (!Objects.equals(userRegistrationRequest.getPassword(), userRegistrationRequest.getConfirmPassword())) {
            return Mono.error(new RuntimeException("password distinguish from confirm_password"));
        }
        return Mono.empty();
    }

    private UserRepresentation toUserRepresentation(UserRegistrationRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(Boolean.TRUE);
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmailVerified(Boolean.TRUE);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setValue(request.getPassword());
        credential.setTemporary(Boolean.FALSE);
        credential.setType(CredentialRepresentation.PASSWORD);

        user.setCredentials(Collections.singletonList(credential));
        return user;
    }

    private UserAuthenticationResponse toUserAuthenticationResponse(AccessTokenResponse token) {
        UserAuthenticationResponse response = new UserAuthenticationResponse();
        response.setAccessToken(token.getToken());
        response.setExpiresIn(token.getExpiresIn());
        response.setRefreshToken(token.getRefreshToken());
        response.setTokenType(token.getTokenType());
        return response;
    }
}
