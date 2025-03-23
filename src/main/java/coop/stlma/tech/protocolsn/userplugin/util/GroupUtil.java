package coop.stlma.tech.protocolsn.userplugin.util;

import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;
import coop.stlma.tech.protocolsn.registration.model.UserGroup;

import java.util.UUID;

/**
 * Utilities for working with groups
 */
public class GroupUtil {
    private GroupUtil() {}

    /**
     * Get a given attribute out of the group representation, handling nulls along the way
     * @param groupRepresentation   Keycloak group representation
     * @param attributeName         Attribute to find
     * @return                      Attribute value as string, null if the attribute is not set
     */
    public static String findAttribute(GroupRepresentation groupRepresentation, String attributeName) {
        if (groupRepresentation.getAttributes() != null &&
                groupRepresentation.getAttributes().containsKey(attributeName) &&
                !groupRepresentation.getAttributes().get(attributeName).isEmpty()) {
            return groupRepresentation.getAttributes().get(attributeName).getFirst();
        }
        return null;
    }

    /**
     * Get a given attribute out of a group representation, handling nulls along the way. Convenience method of #findAttribute
     * @param groupRepresentation   Keycloak group representation
     * @param attributeName         Attribute to find
     * @return                      Attribute value as boolean, null if the attribute is not set
     */
    public static Boolean findAttributeAsBoolean(GroupRepresentation groupRepresentation, String attributeName) {
        return Boolean.parseBoolean(findAttribute(groupRepresentation, attributeName));
    }

    /**
     * Translate the keycloak group representation to a standard UserGroup
     * @param groupRepresentation   Keycloak group representation
     * @return                      Standard UserGroup
     */
    public static UserGroup toUserGroup(GroupRepresentation groupRepresentation) {
        UserGroup userGroup = new UserGroup();
        userGroup.setId(UUID.fromString(groupRepresentation.getId()));
        userGroup.setGroupName(groupRepresentation.getName());
        userGroup.setNewUserDefault(findAttributeAsBoolean(groupRepresentation, "default"));
        return userGroup;
    }
}
