package coop.stlma.tech.protocolsn.registration.api;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static coop.stlma.tech.protocolsn.registration.api.UserOperations.NODE_USER_ADMIN;

public interface UserGroupsOperations {
    String NODE_USER_GROUP_MANAGEMENT_ROLE = NODE_USER_ADMIN;

    String ADD_USER_TO_GROUP_PATH = "/v0.3.0/user-groups/{userId}/group/{groupId}";
    Mono<HttpResponse<Void>> addUserToGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId);
    String REMOVE_USER_FROM_GROUP_PATH = "/v0.3.1/user-groups/{userId}/group/{groupId}";
    Mono<HttpResponse<Void>> removeUserFromGroup(@PathVariable("userId") UUID userId, @PathVariable("groupId") UUID groupId);
}
