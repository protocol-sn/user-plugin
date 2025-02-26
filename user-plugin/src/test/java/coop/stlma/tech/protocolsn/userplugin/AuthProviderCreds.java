package coop.stlma.tech.protocolsn.userplugin;

import coop.stlma.tech.protocolsn.registration.api.UserOperations;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

import java.util.List;
import java.util.Map;

@Singleton
public class AuthProviderCreds<B> implements HttpRequestAuthenticationProvider<B> {
    @Override
    public @NonNull AuthenticationResponse authenticate(@Nullable HttpRequest<B> requestContext,
                                                        @NonNull AuthenticationRequest<String, String> authRequest) {
        if (authRequest.getIdentity().equals("AdminUser") && authRequest.getSecret().equals("AdminPass")) {
            return AuthenticationResponse.success("AdminUser", Map.of("realm_access", Map.of("roles", List.of(UserOperations.NODE_USER_ADMIN))));
        }
        if (authRequest.getIdentity().equals("TestUser") && authRequest.getSecret().equals("TestPass")) {
            return AuthenticationResponse.success("TestUser");
        }
        return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
    }
}
