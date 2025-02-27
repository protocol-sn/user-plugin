package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import io.micronaut.context.annotation.Primary;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.UUID;

@MicronautTest
class UserGroupsServiceImplTest {
    private static final UUID USER_ID = UUID.nameUUIDFromBytes("groupedUser".getBytes());
    private static final UUID GROUP_ID = UUID.nameUUIDFromBytes("someGroup".getBytes());

    @MockBean
    @Primary
    KeycloakAdminClient keycloakApiMock = Mockito.mock(KeycloakAdminClient.class);

    @Inject
    UserGroupsServiceImpl userService;

    @Test
    void testAddUserToGroup_happyPath() {
        Mockito.when(keycloakApiMock.addUserToGroup("social-network-ecosystem", GROUP_ID.toString(), USER_ID.toString()))
                .thenReturn(Mono.empty());
        
        userService.addUserToGroup(USER_ID, GROUP_ID).block();

        Mockito.verify(keycloakApiMock).addUserToGroup("social-network-ecosystem", GROUP_ID.toString(), USER_ID.toString());
    }
}
