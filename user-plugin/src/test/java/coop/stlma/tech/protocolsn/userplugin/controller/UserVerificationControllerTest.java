package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.registration.api.UserOperations;
import coop.stlma.tech.protocolsn.registration.api.UserVerificationOperations;
import coop.stlma.tech.protocolsn.userplugin.TestUtil;
import coop.stlma.tech.protocolsn.userplugin.error.UserManagementException;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import coop.stlma.tech.protocolsn.userplugin.service.UserVerificationService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.micronaut.web.router.UriRouteInfo;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.UUID;

@MicronautTest
class UserVerificationControllerTest {
    public static UUID userId = UUID.nameUUIDFromBytes("controllerVerifyUserId".getBytes());

    @MockBean
    @Primary
    UserVerificationService userServiceMock = Mockito.mock(UserVerificationService.class);

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void testVerifyUser_happyPath() {
        Mockito.when(userServiceMock.verifyUser(userId)).thenReturn(Mono.empty());

        HttpRequest<?> request = HttpRequest.PUT(UserVerificationOperations.VERIFY_USER_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }

    @Test
    void testVerifyUser_noRoles() {

        HttpRequest<?> finalRequest = HttpRequest.PUT(UserVerificationOperations.VERIFY_USER_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testRequestVerificationUser_alreadyVerified() {
        Mockito.when(userServiceMock.requestVerification(userId)).thenReturn(Mono.error(new UserManagementException("User is already verified")));

        HttpRequest<?> request = HttpRequest.POST(UserVerificationOperations.REQUEST_VERIFICATION_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient))
                .accept(MediaType.APPLICATION_JSON);

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(request));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
        Assertions.assertEquals("User is already verified", result.getResponse().getBody(String.class).get());
    }

    @Test
    void testRequestVerificationUser_happyPath() {
        Mockito.when(userServiceMock.requestVerification(userId)).thenReturn(Mono.empty());

        HttpRequest<?> request = HttpRequest.POST(UserVerificationOperations.REQUEST_VERIFICATION_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient))
                .accept(MediaType.APPLICATION_JSON);

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }
}
