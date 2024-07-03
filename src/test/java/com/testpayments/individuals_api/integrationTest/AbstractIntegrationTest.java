package com.testpayments.individuals_api.integrationTest;


import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    private static final String REALM_EXPORT_PATH = "/realm-export.json";
    private static final String REALM_NAME = "Alibou";
    private static final String CLIENT_ID = "my-api";
    private static final String CLIENT_SECRET = "SJJ5NFboYCPDb80bdjnDeukkatF11Ucj";

    @Container
    public static KeycloakContainer keycloakContainer = new KeycloakContainer("quay.io/keycloak/keycloak:25.0.0")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withRealmImportFile(REALM_EXPORT_PATH);

    static {
        keycloakContainer.start();
    }

    @DynamicPropertySource
    static void keycloakProperties(DynamicPropertyRegistry registry) {
        registry.add("keycloak.urls.auth", keycloakContainer::getAuthServerUrl);
        registry.add("keycloak.realm", () -> REALM_NAME);
        registry.add("keycloak.clientId", () -> CLIENT_ID);
        registry.add("keycloak.clientSecret", () -> CLIENT_SECRET);
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/Alibou");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/Alibou/protocol/openid-connect/certs");
        registry.add("keycloak-service.client.base-admin-url", () -> keycloakContainer.getAuthServerUrl() + "/admin/realms/Alibou");
    }
}
