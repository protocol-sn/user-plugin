package coop.stlma.tech.protocolsn.userplugin.util;

import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;

public class GroupUtil {
    private GroupUtil() {}

    public static String findAttribute(GroupRepresentation groupRepresentation, String attributeName) {
        if (groupRepresentation.getAttributes() != null &&
                groupRepresentation.getAttributes().containsKey(attributeName) &&
                !groupRepresentation.getAttributes().get(attributeName).isEmpty()) {
            return groupRepresentation.getAttributes().get(attributeName).getFirst();
        }
        return null;
    }

    public static Boolean findAttributeAsBoolean(GroupRepresentation groupRepresentation, String attribute) {
        if (groupRepresentation.getAttributes() != null && groupRepresentation.getAttributes().containsKey(attribute) && !groupRepresentation.getAttributes().get(attribute).isEmpty()) {
            return Boolean.parseBoolean(groupRepresentation.getAttributes().get(attribute).getFirst());
        }
        return null;
    }
}
