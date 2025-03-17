package coop.stlma.tech.protocolsn.userplugin.error;

/**
 * Generic exception for when something goes wrong while doing user management
 *
 * @author John Meyerin
 */
public class UserManagementException extends RuntimeException {
    public UserManagementException(String message) {
        super(message);
    }
}
