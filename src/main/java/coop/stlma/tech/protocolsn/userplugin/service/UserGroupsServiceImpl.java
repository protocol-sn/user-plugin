package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;
import coop.stlma.tech.protocolsn.userplugin.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.model.UserGroup;
import coop.stlma.tech.protocolsn.userplugin.util.GroupUtil;
import coop.stlma.tech.protocolsn.userplugin.util.ParseToQUtil;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of @link{UserGroupsService}
 *
 * @author John Meyerin
 */
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

    /**
     * Add a given user to a given group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          empty
     */
    @Override
    public Mono<Void> addUserToGroup(UUID userId, UUID groupId) {
        return keycloakAdminClient.addUserToGroup(keycloakRealm, userId.toString(), groupId.toString())
                .then();
    }

    /**
     * Remove a user from a given group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          empty
     */
    @Override
    public Mono<Void> removeUserFromGroup(UUID userId, UUID groupId) {
        return keycloakAdminClient.removeUserFromGroup(keycloakRealm, userId.toString(), groupId.toString())
                .then();
    }

    /**
     * Add a group to the list of default groups
     * @param groupId   Id of the group
     * @return          empty
     */
    @Override
    public Mono<Void> addGroupToDefaults(UUID groupId) {
        return setGroupAttribute(groupId, "default", List.of("true"));
    }

    /**
     * Remove a group from the list of default groups
     * @param groupId   Id of the group
     * @return          empty
     */
    @Override
    public Mono<Void> removeGroupFromDefault(UUID groupId) {
        return setGroupAttribute(groupId, "default", List.of("false"));
    }

    /**
     * Query the groups based on a given set of criteria
     * @param query     Query criteria
     * @return          Groups who meet the query criteria
     */
    @Override
    public Flux<UserGroup> queryGroups(GroupQueryCriteria query) {
        return keycloakAdminClient.queryGroups(keycloakRealm,
                null, null, query.getOffset(), query.getLimit(), null, ParseToQUtil.parseToQ(query), null)
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

    /**
     * Set the value of an attribute for a group, or create the attribute if it doesn't exist
     * @param groupId       Id of the group
     * @param attribute     Name of the attribute
     * @param value         New value of the attribute
     * @return              empty
     */
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
