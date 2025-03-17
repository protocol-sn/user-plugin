package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;
import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.util.GroupUtil;
import coop.stlma.tech.protocolsn.userplugin.util.UserUtil;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of @link{UserService}
 *
 * @author John Meyerin
 */
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

    /**
     * Flag the given user as approved
     * @param userId    Id of the user
     * @return          empty
     */
    @Override
    public Mono<Void> approveUser(UUID userId) {
        return setUserAttribute(userId, "approved", List.of("true"));
    }

    /**
     * Query the users based on a given set of criteria
     * @param query     Query criteria
     * @return          Users who meet the query criteria
     */
    @Override
    public Flux<PsnUser> queryUsers(UserQueryCriteria query) {
        return keycloakAdminClient.queryUsers(keycloakRealm, null, null, null, null, null,
                query.getOffset() == null ? 0 : query.getOffset(), null, null, null, null,
                query.getLimit() == null ? 25 : query.getLimit(), query.parseToQ(), query.getSearch(), null)
                .map(listHttpResponse -> {
                    log.debug("Got {} users", listHttpResponse.body().size());
                    return listHttpResponse.body();
                })
                .flatMapIterable(userRepresentations -> {
                    List<PsnUser> users = new ArrayList<>();
                    userRepresentations.forEach(userRepresentation -> {
                        log.debug("Got user: {}", userRepresentation.getUsername());
                        users.add(UserUtil.representationToUser(userRepresentation));
                    });
                    return users;
                })
                .flatMap(this::addGroupsToUser);
    }

    /**
     * Set the value of an attribute for a user, or create the attribute if it doesn't exist
     * @param userId    Id of the user
     * @param attribute Attribute to set/change the value of
     * @param value     New value of the attribnute
     * @return          empty
     */
    @Override
    public Mono<Void> setUserAttribute(UUID userId, String attribute, List<String> value) {
        return keycloakAdminClient.getUser(keycloakRealm, userId.toString())
                .map(userRepresentationHttpResponse -> {
                    log.debug("Got user {} to modify attribute {}", userRepresentationHttpResponse.body().getUsername(), attribute);
                    UserRepresentation user = userRepresentationHttpResponse.body();
                    if (user.getAttributes() == null) {
                        user.setAttributes(new HashMap<>());
                    }
                    user.getAttributes().put(attribute, value);
                    return user;
                })
                .flatMap(userRepresentation -> {
                    log.debug("New user attributes: {}", userRepresentation.getAttributes());
                    return keycloakAdminClient.updateUser(keycloakRealm, userId.toString(), userRepresentation);
                }).then();
    }

    /**
     * Get a user by id
     * @param userId    Id of the user
     * @return          The requested user
     */
    @Override
    public Mono<PsnUser> getUser(UUID userId) {
        return keycloakAdminClient.getUser(keycloakRealm, userId.toString())
            .map(userRepresentationHttpResponse -> {
                UserRepresentation userRepresentation = userRepresentationHttpResponse.body();
                log.debug("Got user: {}", userRepresentation.getUsername());
                return UserUtil.representationToUser(userRepresentation);

            })
            .flatMap(this::addGroupsToUser);
    }

    private Mono<PsnUser> addGroupsToUser(PsnUser psnUser) {
        return keycloakAdminClient.getUserGroups(keycloakRealm, psnUser.getId(), true, 0, 25, null)
                .map(listHttpResponse -> {
                    List<GroupRepresentation> responseBody = listHttpResponse.body();
                    psnUser.setGroupMembership(responseBody.stream().map(GroupUtil::toUserGroup).toList());
                    return psnUser;
                });
    }


}
