package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.userplugin.error.UserManagementException;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@MicronautTest
class UserVerificationServiceImplTest {

    private static final UUID USER_ID = UUID.nameUUIDFromBytes("verifyingUser".getBytes());

    @MockBean
    @Primary
    KeycloakAdminClient keycloakApiMock = Mockito.mock(KeycloakAdminClient.class);

    @MockBean
    @Primary
    UserService userServiceMock = Mockito.mock(UserService.class);

    @Inject
    UserVerificationServiceImpl userVerificationService;

    @Test
    void testRequestVerification_userAlreadyVerified() {
        UserRepresentation returnedUser = new UserRepresentation();
        returnedUser.setAttributes(Map.of("verified", List.of("true")));

        Mockito.when(keycloakApiMock.getUser("social-network-ecosystem",
                        USER_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(returnedUser)));


        StepVerifier.create(userVerificationService.requestVerification(USER_ID))
                .expectError(UserManagementException.class)
                .verify();
    }

    @Test
    void testRequestVerification_happyPath() {
        UserRepresentation returnedUser = new UserRepresentation();

        Mockito.when(keycloakApiMock.getUser("social-network-ecosystem",
                        USER_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(returnedUser)));

        Mockito.when(userServiceMock.setUserAttribute(USER_ID, "requests-verification", List.of("true")))
                .thenReturn(Mono.empty());

        userVerificationService.requestVerification(USER_ID).block();

        Mockito.verify(userServiceMock).setUserAttribute(USER_ID, "requests-verification", List.of("true"));
    }

    @Test
    void testVerifyUser_happyPath() {
        Mockito.when(userServiceMock.setUserAttribute(USER_ID, "verified", List.of("true")))
                .thenReturn(Mono.empty());

        Mockito.when(userServiceMock.setUserAttribute(USER_ID, "requests-verification", List.of("false")))
                .thenReturn(Mono.empty());

        userVerificationService.verifyUser(USER_ID).block();

        Mockito.verify(userServiceMock).setUserAttribute(USER_ID, "verified", List.of("true"));
        Mockito.verify(userServiceMock).setUserAttribute(USER_ID, "requests-verification", List.of("false"));
    }
}
