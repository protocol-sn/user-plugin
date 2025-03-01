package coop.stlma.tech.protocolsn.userplugin.util;

import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;

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
}
