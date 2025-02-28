package coop.stlma.tech.protocolsn.userplugin.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserGroupsService {
    Mono<Void> addUserToGroup(UUID userId, UUID groupId);

    Mono<Void> removeUserFromGroup(UUID userId, UUID groupId);
}
