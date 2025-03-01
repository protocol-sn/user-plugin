package coop.stlma.tech.protocolsn.registration.api;

import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static coop.stlma.tech.protocolsn.registration.api.UserOperations.NODE_USER_ADMIN;

public interface UserVerificationOperations {

    String VERIFY_USER_PATH = "/v0.3.2/users/verify/{userId}";
    String VERIFY_USER_ROLE = NODE_USER_ADMIN;
    Mono<HttpResponse<Void>> verifyUser(@PathVariable("userId") UUID userId);
    String REQUEST_VERIFICATION_PATH = "/v0.3.3/users/verify/{userId}";
    Mono<HttpResponse<Void>> requestVerification(@PathVariable("userId") UUID userId);
}
