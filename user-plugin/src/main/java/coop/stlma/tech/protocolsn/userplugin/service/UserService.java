package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserService {

    Mono<Void> approveUser(UUID userId);

    Flux<PsnUser> queryUsers(UserQueryCriteria query);
}
