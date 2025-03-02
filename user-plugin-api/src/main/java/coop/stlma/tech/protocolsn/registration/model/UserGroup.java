package coop.stlma.tech.protocolsn.registration.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Data;

import java.util.UUID;

@Serdeable
@Data
public class UserGroup {
    private UUID id;
    private String groupName;
    private Boolean newUserDefault;
}
