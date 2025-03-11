package coop.stlma.tech.protocolsn.registration.api;

import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserOperations {

    String NODE_USER_ADMIN = "node-user-admin";

    String APPROVE_ROLE = NODE_USER_ADMIN;
    String APPROVE_PATH = "/users/approve/{userId}";
    Mono<HttpResponse<Void>> approveUser(@PathVariable("userId") UUID userId);

    String QUERY_PATH = "/users/query";
    String QUERY_ROLE = NODE_USER_ADMIN;

    Mono<HttpResponse<List<PsnUser>>> queryUsers(UserQueryCriteria query);

    String PENDING_APPROVAL_PATH = "/users/pending-approval";
    String PENDING_APPROVAL_ROLE = NODE_USER_ADMIN;
    Mono<HttpResponse<List<PsnUser>>> usersPendingApproval(Integer limit, Integer offset);

    String GET_USER_PATH = "/users/{userId}";
    Mono<HttpResponse<PsnUser>> getUser(@PathVariable("userId") UUID userId);
}
