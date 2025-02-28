package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.registration.api.UserGroupsOperations;
import coop.stlma.tech.protocolsn.userplugin.service.UserGroupsService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import reactor.core.publisher.Mono;

import java.util.UUID;

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
}
