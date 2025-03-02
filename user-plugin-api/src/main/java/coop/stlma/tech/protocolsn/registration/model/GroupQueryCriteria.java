package coop.stlma.tech.protocolsn.registration.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Serdeable
public class GroupQueryCriteria {
    private Integer offset;
    private Integer limit;
    private Boolean defaultUserGroup;

    public String parseToQ() {
        StringBuilder sb = new StringBuilder();
        if (defaultUserGroup != null) {
            sb.append("default:").append(defaultUserGroup).append(" ");
        }
        return sb.toString().trim();
    }
}
