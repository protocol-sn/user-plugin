package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.userplugin.error.UserManagementException;
import coop.stlma.tech.protocolsn.userplugin.util.UserUtil;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Singleton
public class UserVerificationServiceImpl implements UserVerificationService {

    private final UserService userService;
    private final KeycloakAdminClient keycloakAdminClient;
    private final String keycloakRealm;

    public UserVerificationServiceImpl(UserService userService,
                                       KeycloakAdminClient keycloakAdminClient,
                                       @Value("${coop.stlma.tech.protocolsn.keycloak.realm:social-network-ecosystem}") String keycloakRealm) {
        this.userService = userService;
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakRealm = keycloakRealm;
    }

    @Override
    public Mono<Void> verifyUser(UUID userId) {
        return userService.setUserAttribute(userId, "verified", List.of("true"))
                .then(Mono.defer(() ->
                        userService.setUserAttribute(userId, "requests-verification", List.of("false"))));
    }

    @Override
    public Mono<Void> requestVerification(UUID userId) {
        return keycloakAdminClient.getUser(keycloakRealm, userId.toString())
                .map(HttpResponse::body)
                .flatMap(userRepresentation -> {
                    if (UserUtil.findAttributeAsBoolean(userRepresentation, "verified") == Boolean.TRUE) {
                        return Mono.error(new UserManagementException("User is already verified"));
                    }
                    return userService.setUserAttribute(userId, "requests-verification", List.of("true"));
                })
                .then();
    }
}
