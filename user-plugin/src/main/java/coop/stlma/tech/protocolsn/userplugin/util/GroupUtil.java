package coop.stlma.tech.protocolsn.userplugin.util;

import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;
import coop.stlma.tech.protocolsn.registration.model.UserGroup;

import java.util.UUID;

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
        return Boolean.parseBoolean(findAttribute(groupRepresentation, attribute));
    }

    public static UserGroup toUserGroup(GroupRepresentation groupRepresentation) {
        UserGroup userGroup = new UserGroup();
        userGroup.setId(UUID.fromString(groupRepresentation.getId()));
        userGroup.setGroupName(groupRepresentation.getName());
        userGroup.setNewUserDefault(findAttributeAsBoolean(groupRepresentation, "default"));
        return userGroup;
    }
}
