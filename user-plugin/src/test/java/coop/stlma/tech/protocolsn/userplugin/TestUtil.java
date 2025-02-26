package coop.stlma.tech.protocolsn.userplugin;

import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;

public class TestUtil {

    public static UserRepresentation buildUserRepresentation(String username) {
        UserRepresentation returnMe = new UserRepresentation();
        returnMe.setUsername(username);
        return returnMe;
    }
}
