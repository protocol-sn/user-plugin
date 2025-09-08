package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.userplugin.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.model.UserGroup;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Methods for interacting with users and groups
 *
 * @author John Meyerin
 */
public interface UserGroupsService {

    /**
     * Add a given user to a given group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          empty
     */
    Mono<Void> addUserToGroup(UUID userId, UUID groupId);

    /**
     * Remove a user from a given group
     * @param userId    Id of the user
     * @param groupId   Id of the group
     * @return          empty
     */
    Mono<Void> removeUserFromGroup(UUID userId, UUID groupId);

    /**
     * Add a group to the list of default groups
     * @param groupId   Id of the group
     * @return          empty
     */
    Mono<Void> addGroupToDefaults(UUID groupId);

    /**
     * Remove a group from the list of default groups
     * @param groupId   Id of the group
     * @return          empty
     */
    Mono<Void> removeGroupFromDefault(UUID groupId);

    /**
     * Query the groups based on a given set of criteria
     * @param query     Query criteria
     * @return          Groups who meet the query criteria
     */
    Flux<UserGroup> queryGroups(GroupQueryCriteria query);

    /**
     * Set the value of an attribute for a group, or create the attribute if it doesn't exist
     * @param groupId       Id of the group
     * @param attribute     Name of the attribute
     * @param value         New value of the attribute
     * @return              empty
     */
    Mono<Void> setGroupAttribute(UUID groupId, String attribute, List<String> value);
}
