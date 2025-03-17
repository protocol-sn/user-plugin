package coop.stlma.tech.protocolsn.registration.api;

import coop.stlma.tech.protocolsn.registration.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.registration.model.UserGroup;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static coop.stlma.tech.protocolsn.registration.api.UserOperations.NODE_USER_ADMIN;

/**
 * Endpoints for interactions between users and groups
 *
 * @author John Meyerin
 */
public interface UserGroupsOperations {
    String NODE_USER_GROUP_MANAGEMENT_ROLE = NODE_USER_ADMIN;

    String ADD_USER_TO_GROUP_PATH = "/user-groups/{userId}/group/{groupId}";
    /**
     * Add the given user to the given group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          200 OK
     */
    Mono<HttpResponse<Void>> addUserToGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId);
    String REMOVE_USER_FROM_GROUP_PATH = "/user-groups/{userId}/group/{groupId}";
    /**
     * Remove a user from a group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          200 OK
     */
    Mono<HttpResponse<Void>> removeUserFromGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId);

    String ADD_GROUP_TO_DEFAULTS = "/user-groups/defaults/{groupId}";

    /**
     * Add a group to the groups considered default for a user on approval
     * @param groupId   Id of the group
     * @return          200 OK
     */
    Mono<HttpResponse<Void>> addGroupsToDefaults(@PathVariable("groupId") UUID groupId);
    String REMOVE_GROUP_FROM_DEFAULTS = ADD_GROUP_TO_DEFAULTS;

    /**
     * Remove a group from the groups considered default for a user on approval
     * @param groupId   Id of the group
     * @return          200 OK
     */
    Mono<HttpResponse<Void>> removeGroupsFromDefaults(@PathVariable("groupId") UUID groupId);
    String GET_DEFAULT_GROUPS_PATH = "/user-groups/defaults";
    Mono<HttpResponse<List<UserGroup>>> getDefaultGroups();

    String GET_GROUPS_PATH = "/user-groups";

    /**
     * Get list of groups designated as default for a newly approved user. Convenience method of @link{#getGroups(GroupQueryCriteria)}
     * @return  List of groups
     */
    Mono<HttpResponse<List<UserGroup>>> getGroups(GroupQueryCriteria query);
}
