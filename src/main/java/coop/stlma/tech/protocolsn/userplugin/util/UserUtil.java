package coop.stlma.tech.protocolsn.userplugin.util;

import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.userplugin.UserData;
import coop.stlma.tech.protocolsn.userplugin.UserGroupData;
import coop.stlma.tech.protocolsn.userplugin.model.PsnUser;

import java.util.Collections;
import java.util.Optional;

/**
 * Utilities for working with users
 */
public class UserUtil {
    private UserUtil() {}

    /**
     * Get a given attribute out of the user representation, handling nulls along the way
     * @param userRepresentation    Keycloak user representation
     * @param attributeName         Attribute to find
     * @return                      Attribute value as string, null if the attribute is not set
     */
    public static String findAttribute(UserRepresentation userRepresentation, String attributeName) {
        if (userRepresentation.getAttributes() != null &&
                userRepresentation.getAttributes().containsKey(attributeName) &&
                !userRepresentation.getAttributes().get(attributeName).isEmpty()) {
            return userRepresentation.getAttributes().get(attributeName).getFirst();
        }
        return null;
    }

    /**
     * Get a given attribute out of a group representation, handling nulls along the way. Convenience method of #findAttribute
     * @param userRepresentation   Keycloak group representation
     * @param attributeName         Attribute to find
     * @return                      Attribute value as boolean, null if the attribute is not set
     */
    public static Boolean findAttributeAsBoolean(UserRepresentation userRepresentation, String attributeName) {
        return Boolean.parseBoolean(findAttribute(userRepresentation, attributeName));
    }

    /**
     * Translate the keycloak user representation to a standard PsnUser
     * @param userRepresentation    Keycloak user representation
     * @return                      Standard UserGroup
     */
    public static PsnUser representationToUser(UserRepresentation userRepresentation) {
        return PsnUser.builder()
                .id(userRepresentation.getId())
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .givenName(findAttribute(userRepresentation, "given_name"))
                .familyName(findAttribute(userRepresentation, "family_name"))
                .approved(findAttributeAsBoolean(userRepresentation, "approved"))
                .verified(findAttributeAsBoolean(userRepresentation, "verified"))
                .requestsVerification(findAttributeAsBoolean(userRepresentation, "requests-verification"))
                .build();
    }

    /**
     * Translate the model user to the grpc user data
     * @param psnUser   User as model
     * @return          User as grpc data
     */
    public static UserData userDataFromPsnUser(PsnUser psnUser) {
        return UserData.newBuilder()
                .setId(psnUser.getId())
                .setUsername(psnUser.getUsername())
                .setEmail(Optional.ofNullable(psnUser.getEmail()).orElse(""))
                .setGivenName(Optional.ofNullable(psnUser.getGivenName()).orElse(""))
                .setFamilyName(Optional.ofNullable(psnUser.getFamilyName()).orElse(""))
                .setApproved(Optional.ofNullable(psnUser.getApproved()).orElse(false))
                .setVerified(Optional.ofNullable(psnUser.getVerified()).orElse(false))
                .setRequestsVerification(Optional.ofNullable(psnUser.getRequestsVerification()).orElse(false))
                .addAllGroupMembership(Optional.ofNullable(psnUser.getGroupMembership()).orElse(Collections.emptyList())
                        .stream()
                        .map(userGroup -> UserGroupData.newBuilder()
                                .setId(userGroup.getId().toString())
                                .setGroupName(userGroup.getGroupName())
                                .setNewUserDefault(userGroup.getNewUserDefault())
                                .build())
                        .toList())
                .build();
    }
}
