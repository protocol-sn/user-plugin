package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.registration.api.UserOperations;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import jakarta.annotation.security.RolesAllowed;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Controller
public class UserController implements UserOperations {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Put(UserOperations.APPROVE_PATH)
    @RolesAllowed(UserOperations.APPROVE_ROLE)
    @Override
    public Mono<HttpResponse<Void>> approveUser(@PathVariable("userId") UUID userId) {
        return userService.approveUser(userId)
                .thenReturn(HttpResponse.ok());
    }

    @Post(UserOperations.QUERY_PATH)
    @RolesAllowed(UserOperations.QUERY_ROLE)
    @Override
    public Mono<HttpResponse<List<PsnUser>>> queryUsers(@Body UserQueryCriteria query) {
        return userService.queryUsers(query)
                .collectList()
                .map(HttpResponse::ok);
    }

    @Get(UserOperations.PENDING_APPROVAL_PATH)
    @RolesAllowed(UserOperations.PENDING_APPROVAL_ROLE)
    @Override
    public Mono<HttpResponse<List<PsnUser>>> usersPendingApproval(@QueryValue(value = "limit", defaultValue = "25") Integer limit,
                                                                  @QueryValue(value = "offset", defaultValue = "0") Integer offset) {
        UserQueryCriteria query = UserQueryCriteria.builder()
                .limit(limit)
                .offset(offset)
                .approved(false)
                .build();
        return userService.queryUsers(query)
                .collectList()
                .map(HttpResponse::ok);
    }

    @Put(UserOperations.VERIFY_USER_PATH)
    @RolesAllowed(UserOperations.VERIFY_USER_ROLE)
    @Override
    public Mono<HttpResponse<Void>> verifyUser(@PathVariable("userId") UUID userId) {
        return userService.verifyUser(userId)
                .thenReturn(HttpResponse.ok());
    }
}
