package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Singleton
public class UserGroupsServiceImpl implements UserGroupsService {

    private final KeycloakAdminClient keycloakAdminClient;
    private final String keycloakRealm;

    public UserGroupsServiceImpl(KeycloakAdminClient keycloakAdminClient,
                                 @Value("${coop.stlma.tech.protocolsn.keycloak.realm:social-network-ecosystem}") String keycloakRealm) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakRealm = keycloakRealm;
    }

    @Override
    public Mono<Void> addUserToGroup(UUID userId, UUID groupId) {
        return keycloakAdminClient.addUserToGroup(keycloakRealm, userId.toString(), groupId.toString())
                .then();
    }
}
