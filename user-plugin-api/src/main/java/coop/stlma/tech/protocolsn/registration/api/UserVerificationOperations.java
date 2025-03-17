package coop.stlma.tech.protocolsn.registration.api;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints for user verification
 *
 * @author John Meyerin
 */
public interface UserVerificationOperations {

    String VERIFY_USER_PATH = "/users/verify/{userId}";
    String VERIFY_USER_ROLE = "verifier";

    /**
     * Flag the given user as verified
     *
     * @param userId    Id of the user
     * @return          200 OK
     */
    Mono<HttpResponse<Void>> verifyUser(@PathVariable("userId") UUID userId);
    String REQUEST_VERIFICATION_PATH = "/users/verify/{userId}";

    /**
     * Flag the given user as needing verification
     * @param userId    Id of the user
     * @return          200 OK
     */
    Mono<HttpResponse<Void>> requestVerification(@PathVariable("userId") UUID userId);
    String PENDING_VERIFICATION_PATH = "/users/pending-verification";

    /**
     * List users pending verification. Convenience method for @Link{UserController#queryUsers(UserQueryCriteria)}
     * @return  List of users pending verification
     */
    Mono<HttpResponse<List<PsnUser>>> getUserPendingVerification();
}
