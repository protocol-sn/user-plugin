package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;
import coop.stlma.tech.protocolsn.registration.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.registration.model.UserGroup;
import coop.stlma.tech.protocolsn.userplugin.util.GroupUtil;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Singleton
@Slf4j
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

    @Override
    public Mono<Void> removeUserFromGroup(UUID userId, UUID groupId) {
        return keycloakAdminClient.removeUserFromGroup(keycloakRealm, userId.toString(), groupId.toString())
                .then();
    }

    @Override
    public Mono<Void> addGroupToDefaults(UUID groupId) {
        return setGroupAttribute(groupId, "default", List.of("true"));
    }

    @Override
    public Mono<Void> removeGroupFromDefault(UUID groupId) {
        return setGroupAttribute(groupId, "default", List.of("false"));
    }

    @Override
    public Flux<UserGroup> queryGroups(GroupQueryCriteria query) {
        return keycloakAdminClient.queryGroups(keycloakRealm,
                null, null, query.getOffset(), query.getLimit(), null, query.parseToQ(), null)
                .flatMapIterable(listHttpResponse -> {
                    log.debug("Got {} groups", listHttpResponse.body().size());
                    return listHttpResponse.body();
                })
                .map(groupRepresentation -> {
                    log.debug("Got group: {}", groupRepresentation.getName());
                    UserGroup newGroup = new UserGroup();
                    newGroup.setId(UUID.fromString(groupRepresentation.getId()));
                    newGroup.setGroupName(groupRepresentation.getName());
                    newGroup.setNewUserDefault(GroupUtil.findAttributeAsBoolean(groupRepresentation, "default"));
                    return newGroup;
                });
    }

    @Override
    public Mono<Void> setGroupAttribute(UUID groupId, String attribute, List<String> value) {
        return keycloakAdminClient.getGroup(keycloakRealm, groupId.toString())
                .map(groupRepresentationHttpResponse -> {
                    log.debug("Got user {} to modify attribute {}", groupRepresentationHttpResponse.body().getName(), attribute);
                    GroupRepresentation user = groupRepresentationHttpResponse.body();
                    if (user.getAttributes() == null) {
                        user.setAttributes(new HashMap<>());
                    }
                    user.getAttributes().put(attribute, value);
                    return user;
                })
                .flatMap(groupRepresentation -> {
                    log.debug("New user attributes: {}", groupRepresentation.getAttributes());
                    return keycloakAdminClient.updateGroup(keycloakRealm, groupId.toString(), groupRepresentation);
                }).then();
    }
}
