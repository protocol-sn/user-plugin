package coop.stlma.tech.protocolsn.registration.model;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record PsnUser(String id, String username, String email, String givenName, String familyName, Boolean approved) {
}
