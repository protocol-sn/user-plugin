package coop.stlma.tech.protocolsn.userplugin.util;

import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;

public class UserUtil {
    private UserUtil() {}

    public static String findAttribute(UserRepresentation userRepresentation, String attributeName) {
        if (userRepresentation.getAttributes() != null &&
                userRepresentation.getAttributes().containsKey(attributeName) &&
                !userRepresentation.getAttributes().get(attributeName).isEmpty()) {
            return userRepresentation.getAttributes().get(attributeName).getFirst();
        }
        return null;
    }

    public static Boolean findAttributeAsBoolean(UserRepresentation userRepresentation, String attribute) {
        if (userRepresentation.getAttributes() != null && userRepresentation.getAttributes().containsKey(attribute) && !userRepresentation.getAttributes().get(attribute).isEmpty()) {
            return Boolean.parseBoolean(userRepresentation.getAttributes().get(attribute).getFirst());
        }
        return null;
    }

    public static PsnUser represenationToUser(UserRepresentation userRepresentation) {
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
}
