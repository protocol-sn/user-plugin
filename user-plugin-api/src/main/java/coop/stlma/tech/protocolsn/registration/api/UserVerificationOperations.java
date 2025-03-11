package coop.stlma.tech.protocolsn.registration.api;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserVerificationOperations {

    String VERIFY_USER_PATH = "/users/verify/{userId}";
    String VERIFY_USER_ROLE = "verifier";
    Mono<HttpResponse<Void>> verifyUser(@PathVariable("userId") UUID userId);
    String REQUEST_VERIFICATION_PATH = "/users/verify/{userId}";
    Mono<HttpResponse<Void>> requestVerification(@PathVariable("userId") UUID userId);
    String PENDING_VERIFICATION_PATH = "/users/pending-verification";
    Mono<HttpResponse<List<PsnUser>>> getUserPendingVerification();
}
