package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.userplugin.error.UserManagementException;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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

    @Inject
    UserVerificationServiceImpl userService;

    ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);

    @Test
    void testRequestVerification_userAlreadyVerified() {
        UserRepresentation returnedUser = new UserRepresentation();
        returnedUser.setAttributes(Map.of("verified", List.of("true")));

        Mockito.when(keycloakApiMock.getUser("social-network-ecosystem",
                        USER_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(returnedUser)));


        StepVerifier.create(userService.requestVerification(USER_ID))
                .expectError(UserManagementException.class)
                .verify();
    }

    @Test
    void testRequestVerification_happyPath() {
        UserRepresentation returnedUser = new UserRepresentation();

        Mockito.when(keycloakApiMock.getUser("social-network-ecosystem",
                        USER_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(returnedUser)));

        Mockito.when(keycloakApiMock.updateUser(Mockito.eq("social-network-ecosystem"),
                        Mockito.eq(USER_ID.toString()), Mockito.any(UserRepresentation.class)))
                .thenReturn(Mono.empty());

        userService.requestVerification(USER_ID).block();

        Mockito.verify(keycloakApiMock).updateUser(
                Mockito.eq("social-network-ecosystem"),
                Mockito.eq(USER_ID.toString()),
                userCaptor.capture());
    }

    @Test
    void testVerifyUser_happyPath() {
        UserRepresentation returnedUser = new UserRepresentation();

        Mockito.when(keycloakApiMock.getUser("social-network-ecosystem",
                        USER_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(returnedUser)));

        Mockito.when(keycloakApiMock.updateUser(Mockito.eq("social-network-ecosystem"),
                        Mockito.eq(USER_ID.toString()), Mockito.any(UserRepresentation.class)))
                .thenReturn(Mono.empty());

        userService.verifyUser(USER_ID).block();

        Mockito.verify(keycloakApiMock).updateUser(
                Mockito.eq("social-network-ecosystem"),
                Mockito.eq(USER_ID.toString()),
                userCaptor.capture());

        UserRepresentation updatedUser = userCaptor.getValue();

        Assertions.assertTrue(Boolean.parseBoolean(updatedUser.getAttributes().get("verified").get(0)));
    }
}
