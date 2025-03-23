package coop.stlma.tech.protocolsn.userplugin;

import coop.stlma.tech.protocolsn.registration.api.UserGroupsOperations;
import coop.stlma.tech.protocolsn.registration.api.UserOperations;
import coop.stlma.tech.protocolsn.registration.api.UserVerificationOperations;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class AuthProviderCreds<B> implements HttpRequestAuthenticationProvider<B> {

    public static final UUID ADMIN_USER_ID = UUID.nameUUIDFromBytes("AdminUser".getBytes());
    public static final UUID TEST_USER_ID = UUID.nameUUIDFromBytes("TestUser".getBytes());

    @Override
    public @NonNull AuthenticationResponse authenticate(@Nullable HttpRequest<B> requestContext,
                                                        @NonNull AuthenticationRequest<String, String> authRequest) {
        if (authRequest.getIdentity().equals("AdminUser") && authRequest.getSecret().equals("AdminPass")) {
            return AuthenticationResponse.success("AdminUser",
                    Map.of("realm_access",
                            Map.of("roles",
                                    List.of(UserOperations.NODE_USER_ADMIN,
                                            UserGroupsOperations.NODE_USER_GROUP_MANAGEMENT_ROLE,
                                            UserVerificationOperations.VERIFY_USER_ROLE),
                    "sub", ADMIN_USER_ID.toString())));
        }
        if (authRequest.getIdentity().equals("TestUser") && authRequest.getSecret().equals("TestPass")) {
            return AuthenticationResponse.success("TestUser",
                    Map.of("realm_access",
                            Map.of("roles",
                                    Collections.emptyList()),
                            "sub", TEST_USER_ID.toString()));
        }
        return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
    }
}
