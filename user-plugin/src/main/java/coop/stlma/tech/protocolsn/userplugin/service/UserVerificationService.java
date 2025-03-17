package coop.stlma.tech.protocolsn.userplugin.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Set of methods for user verification
 *
 * @author John Meyerin
 */
public interface UserVerificationService {

    /**
     * Flag the given user as verified
     * @param userId    Id of the user
     * @return          empty
     */
    Mono<Void> verifyUser(UUID userId);

    /**
     * Flag the given user as requesting verification
     * @param userId    Id of the user
     * @return          empty
     */
    Mono<Void> requestVerification(UUID userId);
}
