package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.userplugin.api.UserGroupsOperations;
import coop.stlma.tech.protocolsn.userplugin.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.model.UserGroup;
import coop.stlma.tech.protocolsn.userplugin.service.UserGroupsService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static coop.stlma.tech.protocolsn.userplugin.api.UserOperations.NODE_USER_ADMIN;

/**
 * Controller for interactions between users and groups
 *
 * @author John Meyerin
 */
@Controller
public class UserGroupsController implements UserGroupsOperations {

    private final UserGroupsService userGroupsService;

    public UserGroupsController(UserGroupsService userGroupsService) {
        this.userGroupsService = userGroupsService;
    }

    /**
     * Add the given user to the given group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          200 OK
     */
    @Put(ADD_USER_TO_GROUP_PATH)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    @Override
    public Mono<HttpResponse<Void>> addUserToGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId) {
        return userGroupsService.addUserToGroup(userId, groupId)
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Remove a user from a group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          200 OK
     */
    @Delete(REMOVE_USER_FROM_GROUP_PATH)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    @Override
    public Mono<HttpResponse<Void>> removeUserFromGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId) {
        return userGroupsService.removeUserFromGroup(userId, groupId)
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Add a group to the groups considered default for a user on approval
     * @param groupId   Id of the group
     * @return          200 OK
     */
    @Post(ADD_GROUP_TO_DEFAULTS)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    public Mono<HttpResponse<Void>> addGroupsToDefaults(@PathVariable("groupId") UUID groupId) {
        return userGroupsService.addGroupToDefaults(groupId)
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Remove a group from the groups considered default for a user on approval
     * @param groupId   Id of the group
     * @return          200 OK
     */
    @Delete(REMOVE_GROUP_FROM_DEFAULTS)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    @Override
    public Mono<HttpResponse<Void>> removeGroupsFromDefaults(@PathVariable("groupId") UUID groupId) {
        return userGroupsService.removeGroupFromDefault(groupId)
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Get list of groups designated as default for a newly approved user. Convenience method of @link{#getGroups(GroupQueryCriteria)}
     * @return  List of groups
     */
    @Get(GET_DEFAULT_GROUPS_PATH)
    @Secured({NODE_USER_GROUP_MANAGEMENT_ROLE, NODE_USER_ADMIN})
    @Override
    public Mono<HttpResponse<List<UserGroup>>> getDefaultGroups() {
        return userGroupsService.queryGroups(GroupQueryCriteria.builder()
                        .defaultUserGroup(true)
                        .build())
                .collectList()
                .map(HttpResponse::ok);
    }

    /**
     * Query the groups
     * @param query     Query criteria
     * @return          Groups who meet the query criteria
     */
    @Post(GET_GROUPS_PATH)
    @Secured({NODE_USER_GROUP_MANAGEMENT_ROLE, NODE_USER_ADMIN})
    @Override
    public Mono<HttpResponse<List<UserGroup>>> getGroups(@Body GroupQueryCriteria query) {
        return userGroupsService.queryGroups(query)
                .collectList()
                .map(HttpResponse::ok);
    }
}
