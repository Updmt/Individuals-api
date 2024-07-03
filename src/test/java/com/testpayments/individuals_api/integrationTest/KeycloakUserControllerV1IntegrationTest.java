package com.testpayments.individuals_api.integrationTest;

import com.testpayments.individuals_api.dto.UserAuthenticationResponse;
import com.testpayments.individuals_api.dto.UserRegistrationRequest;
import com.testpayments.individuals_api.rest.KeycloakUserControllerV1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

public class KeycloakUserControllerV1IntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void registerUser_200() {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest();
        registrationRequest.setUsername("newUser");
        registrationRequest.setEmail("newUser@example.com");
        registrationRequest.setFirstName("New");
        registrationRequest.setLastName("User");
        registrationRequest.setPassword("Password123");
        registrationRequest.setConfirmPassword("Password123");

        webTestClient.post()
                .uri(KeycloakUserControllerV1.ROOT_URL + "/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registrationRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserAuthenticationResponse.class)
                .consumeWith(response -> {
                    Assertions.assertNotNull(response.getResponseBody());
                    Assertions.assertNotNull(response.getResponseBody().getAccessToken());
                });
    }
}
