package coop.stlma.tech.protocolsn.userplugin.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserVerificationService {

    Mono<Void> verifyUser(UUID userId);

    Mono<Void> requestVerification(UUID userId);
}
