package coop.stlma.tech.protocolsn.registration.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Standardized representation of a PSN user
 *
 * @author John Meyerin
 */
@Serdeable
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PsnUser {
    private String id;
    private String username;
    private String email;
    private String givenName;
    private String familyName;
    private Boolean approved;
    private Boolean verified;
    private Boolean requestsVerification;
    private List<UserGroup> groupMembership;
}
