package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.registration.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.registration.model.UserGroup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UserGroupsService {
    Mono<Void> addUserToGroup(UUID userId, UUID groupId);

    Mono<Void> removeUserFromGroup(UUID userId, UUID groupId);

    Mono<Void> addGroupToDefaults(UUID groupId);
    Mono<Void> removeGroupFromDefault(UUID groupId);
    Flux<UserGroup> queryGroups(GroupQueryCriteria query);
    Mono<Void> setGroupAttribute(UUID groupId, String attribute, List<String> value);
}
