package coop.stlma.tech.protocolsn.userplugin;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@OpenAPIDefinition(info = @Info(
        title = "Protocol SN user plugin",
        version = "0.3.0"
))
@SecuritySchemes(
        @SecurityScheme(
                name = "authenticatedUser",
                type = SecuritySchemeType.OAUTH2,
                flows = @OAuthFlows(
                        password = @OAuthFlow(
                                tokenUrl = "${coop.stlma.tech.protocolsn.security.tokenUrl:http://localhost:9080/realms/spike-realm/protocol/openid-connect/auth}"
                        )
                )
        ))
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}