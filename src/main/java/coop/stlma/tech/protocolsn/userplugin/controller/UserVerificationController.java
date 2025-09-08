package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.userplugin.api.UserVerificationOperations;
import coop.stlma.tech.protocolsn.userplugin.model.PsnUser;
import coop.stlma.tech.protocolsn.userplugin.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.error.UserManagementException;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import coop.stlma.tech.protocolsn.userplugin.service.UserVerificationService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import jakarta.annotation.security.RolesAllowed;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Controller for user verification
 *
 * @author John Meyerin
 */
@Controller
public class UserVerificationController implements UserVerificationOperations {

    private final UserVerificationService userVerificationService;
    private final UserService userService;

    public UserVerificationController(UserVerificationService userVerificationService, UserService userService) {
        this.userVerificationService = userVerificationService;
        this.userService = userService;
    }

    /**
     * Flag the given user as verified
     *
     * @param userId    Id of the user
     * @return          200 OK
     */
    @Put(UserVerificationOperations.VERIFY_USER_PATH)
    @RolesAllowed(UserVerificationOperations.VERIFY_USER_ROLE)
    @Override
    public Mono<HttpResponse<Void>> verifyUser(@PathVariable("userId") UUID userId) {
        return userVerificationService.verifyUser(userId)
                .thenReturn(HttpResponse.ok());
    }

    /**
     * Flag the given user as needing verification
     * @param userId    Id of the user
     * @return          200 OK
     */
    @Post(UserVerificationOperations.REQUEST_VERIFICATION_PATH)
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Override
    public Mono<HttpResponse<Void>> requestVerification(@PathVariable("userId") UUID userId) {
        return userVerificationService.requestVerification(userId)
                .thenReturn(HttpResponse.ok());
    }

    /**
     * List users pending verification. Convenience method for @Link{UserController#queryUsers(UserQueryCriteria)}
     * @return  List of users pending verification
     */
    @Get(UserVerificationOperations.PENDING_VERIFICATION_PATH)
    @RolesAllowed(UserVerificationOperations.VERIFY_USER_ROLE)
    @Override
    public Mono<HttpResponse<List<PsnUser>>> getUserPendingVerification() {
        return userService.queryUsers(UserQueryCriteria.builder()
                        .requestsVerification(true)
                        .build())
                .collectList()
                .map(psnUsers ->
                        HttpResponse.ok(psnUsers)
                                .header("expires", "60")
                                .body(psnUsers));
    }

    @Error(exception = UserManagementException.class)
    public HttpResponse<String> onSavedFailed(HttpRequest<?> request, UserManagementException e) {
        return HttpResponse.badRequest(e.getMessage());
    }
}
