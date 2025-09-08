package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.userplugin.api.UserOperations;
import coop.stlma.tech.protocolsn.userplugin.model.PsnUser;
import coop.stlma.tech.protocolsn.userplugin.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.error.UserLacksRoleException;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.security.utils.SecurityService;
import jakarta.annotation.security.RolesAllowed;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Controller for interactions with users
 *
 * @author John Meyerin
 */
@Controller
public class UserController implements UserOperations {

    private final UserService userService;
    private final SecurityService securityService;

    public UserController(UserService userService,
                          SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
    }

    /**
     * Approve a user
     * @param userId    Id of the user
     * @return          200 OK
     */
    @Put(UserOperations.APPROVE_PATH)
    @RolesAllowed(UserOperations.APPROVE_ROLE)
    @Override
    public Mono<HttpResponse<Void>> approveUser(@PathVariable("userId") UUID userId) {
        return userService.approveUser(userId)
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Query users
     *
     * @param query     Query criteria
     * @return          Users who meet the query criteria
     */
    @Post(UserOperations.QUERY_PATH)
    @RolesAllowed(UserOperations.QUERY_ROLE)
    @Override
    public Mono<HttpResponse<List<PsnUser>>> queryUsers(@Body UserQueryCriteria query) {
        return userService.queryUsers(query)
                .collectList()
                .map(HttpResponse::ok);
    }

    /**
     * List users pending approval. Convenience method for @Link{#queryUsers(UserQueryCriteria)}
     * @param limit     Number of records to retrieve
     * @param offset    Starting offset e.g. page
     * @return          List of users pending approval
     */
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

    /**
     * Get a user by id
     * @param userId    Id of the user
     * @return          The requested user
     */
    @Override
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get(UserOperations.GET_USER_PATH)
    public Mono<HttpResponse<PsnUser>> getUser(@PathVariable("userId") UUID userId) {
        securityService.username().ifPresent(sub -> {
            if (!sub.equals(userId.toString()) && !securityService.hasRole(UserOperations.NODE_USER_ADMIN)) {
                throw new UserLacksRoleException();
            }
        });
        return userService.getUser(userId)
                .map(HttpResponse::ok);
    }
}
