package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

/**
 * Set of methods for interacting with users
 *
 * @author John Meyerin
 */
public interface UserService {

    /**
     * Flag the given user as approved
     * @param userId    Id of the user
     * @return          empty
     */
    Mono<Void> approveUser(UUID userId);

    /**
     * Query the users based on a given set of criteria
     * @param query     Query criteria
     * @return          Users who meet the query criteria
     */
    Flux<PsnUser> queryUsers(UserQueryCriteria query);

    /**
     * Set the value of an attribute for a user, or create the attribute if it doesn't exist
     * @param userId    Id of the user
     * @param attribute Attribute to set/change the value of
     * @param value     New value of the attribnute
     * @return          empty
     */
    Mono<Void> setUserAttribute(UUID userId, String attribute, List<String> value);

    /**
     * Get a user by id
     * @param userId    Id of the user
     * @return          The requested user
     */
    Mono<PsnUser> getUser(UUID userId);
}
