package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Singleton
@Slf4j
public class UserServiceImpl implements UserService {

    private final KeycloakAdminClient keycloakAdminClient;
    private final String keycloakRealm;

    public UserServiceImpl(KeycloakAdminClient keycloakAdminClient,
                           @Value("${coop.stlma.tech.protocolsn.keycloak.realm:social-network-ecosystem}") String keycloakRealm) {
        this.keycloakAdminClient = keycloakAdminClient;
        this.keycloakRealm = keycloakRealm;
    }

    @Override
    public Mono<Void> approveUser(UUID userId) {
        return keycloakAdminClient.getUser(keycloakRealm, userId.toString())
                .map(userRepresentationHttpResponse -> {
                    log.debug("Got user {} to approve", userRepresentationHttpResponse.body().getUsername());
                    UserRepresentation user = userRepresentationHttpResponse.body();
                    if (user.getAttributes() == null) {
                        user.setAttributes(new HashMap<>());
                    }
                    user.getAttributes().put("approved", List.of("true"));
                    return user;
                })
                .flatMap(userRepresentation -> {
                    log.debug("Approving user {}", userRepresentation.getAttributes());
                    return keycloakAdminClient.updateUser(keycloakRealm, userId.toString(), userRepresentation);
                }).then();
    }

    @Override
    public Flux<PsnUser> queryUsers(UserQueryCriteria query) {
        return keycloakAdminClient.queryUsers(keycloakRealm, null, null, null, null, null,
                query.getOffset() == null ? 0 : query.getOffset(), null, null, null, null,
                query.getLimit() == null ? 25 : query.getLimit(), query.parseToQ(), null, null)
                .map(listHttpResponse -> {
                    log.debug("Got {} users", listHttpResponse.body().size());
                    return listHttpResponse.body();
                })
                .flatMapIterable(userRepresentations -> {
                    List<PsnUser> users = new ArrayList<>();
                    userRepresentations.forEach(userRepresentation -> {
                        log.debug("Got user: {}", userRepresentation.getUsername());
                        users.add(new PsnUser(
                                userRepresentation.getId(),
                                userRepresentation.getUsername(),
                                userRepresentation.getEmail(),
                                findAttribute(userRepresentation, "given_name"),
                                findAttribute(userRepresentation, "family_name"),
                                findAttributeAsBoolean(userRepresentation, "approved")));
                    });
                    return users;
                });
    }

    private String findAttribute(UserRepresentation userRepresentation, String givenName) {
        if (userRepresentation.getAttributes() != null && userRepresentation.getAttributes().containsKey(givenName) && !userRepresentation.getAttributes().get(givenName).isEmpty()) {
            return userRepresentation.getAttributes().get(givenName).getFirst();
        }
        return null;
    }

    private Boolean findAttributeAsBoolean(UserRepresentation userRepresentation, String attribute) {
        if (userRepresentation.getAttributes() != null && userRepresentation.getAttributes().containsKey(attribute) && !userRepresentation.getAttributes().get(attribute).isEmpty()) {
            return Boolean.parseBoolean(userRepresentation.getAttributes().get(attribute).getFirst());
        }
        return null;
    }
}
