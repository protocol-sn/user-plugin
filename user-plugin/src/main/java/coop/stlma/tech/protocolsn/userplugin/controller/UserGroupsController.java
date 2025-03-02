package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.pluginlib.security.CommonRoles;
import coop.stlma.tech.protocolsn.registration.api.UserGroupsOperations;
import coop.stlma.tech.protocolsn.registration.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.registration.model.UserGroup;
import coop.stlma.tech.protocolsn.userplugin.service.UserGroupsService;
import io.micronaut.http.HttpResponse;
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

import static coop.stlma.tech.protocolsn.registration.api.UserOperations.NODE_USER_ADMIN;

@Controller
public class UserGroupsController implements UserGroupsOperations {

    private final UserGroupsService userGroupsService;

    public UserGroupsController(UserGroupsService userGroupsService) {
        this.userGroupsService = userGroupsService;
    }

    @Put(ADD_USER_TO_GROUP_PATH)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    @Override
    public Mono<HttpResponse<Void>> addUserToGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId) {
        return userGroupsService.addUserToGroup(userId, groupId)
                .thenReturn(HttpResponse.ok());
    }

    @Delete(REMOVE_USER_FROM_GROUP_PATH)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    @Override
    public Mono<HttpResponse<Void>> removeUserFromGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId) {
        return userGroupsService.removeUserFromGroup(userId, groupId)
                .thenReturn(HttpResponse.ok());
    }

    @Post(ADD_GROUP_TO_DEFAULTS)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    public Mono<HttpResponse<Void>> addGroupsToDefaults(@PathVariable("groupId") UUID groupId) {
        return userGroupsService.addGroupToDefaults(groupId)
                .thenReturn(HttpResponse.ok());
    }

    @Delete(REMOVE_GROUP_FROM_DEFAULTS)
    @Secured(NODE_USER_GROUP_MANAGEMENT_ROLE)
    @Override
    public Mono<HttpResponse<Void>> removeGroupsFromDefaults(@PathVariable("groupId") UUID groupId) {
        return userGroupsService.removeGroupFromDefault(groupId)
                .thenReturn(HttpResponse.ok());
    }

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
}
