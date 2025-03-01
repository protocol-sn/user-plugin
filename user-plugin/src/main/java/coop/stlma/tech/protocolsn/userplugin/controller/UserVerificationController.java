package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.pluginlib.security.CommonRoles;
import coop.stlma.tech.protocolsn.registration.api.UserVerificationOperations;
import coop.stlma.tech.protocolsn.userplugin.error.UserManagementException;
import coop.stlma.tech.protocolsn.userplugin.service.UserVerificationService;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import jakarta.annotation.security.RolesAllowed;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Controller
public class UserVerificationController implements UserVerificationOperations {

    private final UserVerificationService userService;

    public UserVerificationController(UserVerificationService userService) {
        this.userService = userService;
    }

    @Put(UserVerificationOperations.VERIFY_USER_PATH)
    @RolesAllowed(UserVerificationOperations.VERIFY_USER_ROLE)
    @Override
    public Mono<HttpResponse<Void>> verifyUser(@PathVariable("userId") UUID userId) {
        return userService.verifyUser(userId)
                .thenReturn(HttpResponse.ok());
    }

    @Post(UserVerificationOperations.REQUEST_VERIFICATION_PATH)
    @RolesAllowed(CommonRoles.LOGGED_IN_USER)
    @Override
    public Mono<HttpResponse<Void>> requestVerification(@PathVariable("userId") UUID userId) {
        return userService.requestVerification(userId)
                .thenReturn(HttpResponse.ok());
    }

    @Error(exception = UserManagementException.class)
    public HttpResponse<String> onSavedFailed(HttpRequest<?> request, UserManagementException e) {
        return HttpResponse.badRequest(e.getMessage());
    }
}
