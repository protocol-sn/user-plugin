package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.userplugin.model.PsnUser;
import coop.stlma.tech.protocolsn.userplugin.model.UserQueryCriteria;
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
     * @param offset                start page
     * @param limit                 number of records to retrieve
     * @param approved              whether the user is approved
     * @param verified              whether the user is verified
     * @param requestsVerification  whether the user requests verification
     * @param search                search string
     * @return                      Users who meet the query criteria
     */
    Flux<PsnUser> queryUsers(
            Integer offset,
            Integer limit,
            Boolean approved,
            Boolean verified,
            Boolean requestsVerification,
            String search);

    /**
     * Query the users based on a given set of criteria
     * @param query The query
     * @return
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
