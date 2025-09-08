package coop.stlma.tech.protocolsn.userplugin.endpoint;

import coop.stlma.tech.protocolsn.userplugin.SuccessResponse;
import coop.stlma.tech.protocolsn.userplugin.UserReference;
import coop.stlma.tech.protocolsn.userplugin.UserVerificationServiceGrpc;
import coop.stlma.tech.protocolsn.userplugin.error.UserManagementException;
import coop.stlma.tech.protocolsn.userplugin.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import coop.stlma.tech.protocolsn.userplugin.service.UserVerificationService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.UUID;

@MicronautTest
class UserVerificationEndpointTest {
    public static final UUID USER_ID = UUID.nameUUIDFromBytes("controllerVerifyUserId".getBytes());

    @Inject
    UserVerificationServiceGrpc.UserVerificationServiceBlockingStub userVerificationServiceBlockingStub;

    @MockBean
    @Primary
    UserVerificationService userVerificationServiceMock = Mockito.mock(UserVerificationService.class);

    @MockBean
    @Primary
    UserService userServiceMock = Mockito.mock(UserService.class);

    ArgumentCaptor<UserQueryCriteria> criteriaCaptor = ArgumentCaptor.forClass(UserQueryCriteria.class);

    @Test
    void testVerifyUser_happyPath() {
        Mockito.when(userVerificationServiceMock.verifyUser(USER_ID))
                .thenReturn(Mono.empty());

        SuccessResponse response = userVerificationServiceBlockingStub.verifyUser(UserReference.newBuilder().setUserId(USER_ID.toString()).build());

        Assertions.assertTrue(response.getSuccess());
    }

    @Test
    void testRequestVerificationUser_alreadyVerified() {
        Mockito.when(userVerificationServiceMock.requestVerification(USER_ID))
                .thenReturn(Mono.error(new UserManagementException("User is already verified")));

        SuccessResponse response = userVerificationServiceBlockingStub.requestVerification(UserReference.newBuilder().setUserId(USER_ID.toString()).build());

        Assertions.assertFalse(response.getSuccess());

    }

    @Test
    void testRequestVerificationUser_happyPath() {
        Mockito.when(userVerificationServiceMock.requestVerification(USER_ID))
                .thenReturn(Mono.empty());

        SuccessResponse response = userVerificationServiceBlockingStub.requestVerification(UserReference.newBuilder().setUserId(USER_ID.toString()).build());

        Assertions.assertTrue(response.getSuccess());

    }
}
