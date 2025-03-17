package coop.stlma.tech.protocolsn.registration.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Query criteria for groups
 *
 * @author John Meyerin
 */
@Builder
@Getter
@Setter
@Serdeable
public class GroupQueryCriteria {
    private Integer offset;
    private Integer limit;
    private Boolean defaultUserGroup;

    /**
     * Parse the values into the 'q' parameter of the Keycloak query
     * @return  Space-separated string of key/value pairs
     */
    public String parseToQ() {
        StringBuilder sb = new StringBuilder();
        if (defaultUserGroup != null) {
            sb.append("default:").append(defaultUserGroup).append(" ");
        }
        return sb.toString().trim();
    }
}
