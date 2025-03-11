package coop.stlma.tech.protocolsn.registration.model;

import io.micronaut.serde.annotation.Serdeable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@Serdeable
public class UserQueryCriteria {
    private Integer offset;
    private Integer limit;
    private Boolean approved;
    private Boolean verified;
    private Boolean requestsVerification;

    public String parseToQ() {
        StringBuilder sb = new StringBuilder();
        if (approved != null) {
            sb.append("approved:").append(approved).append(" ");
        }
        if (verified != null) {
            sb.append("verified:").append(verified).append(" ");
        }
        if (requestsVerification != null) {
            sb.append("requests-verification:").append(requestsVerification).append(" ");
        }
        return sb.toString().trim();
    }
}
