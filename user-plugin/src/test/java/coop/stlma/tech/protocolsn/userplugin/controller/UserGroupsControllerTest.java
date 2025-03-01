package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.registration.api.UserGroupsOperations;
import coop.stlma.tech.protocolsn.registration.api.UserOperations;
import coop.stlma.tech.protocolsn.userplugin.TestUtil;
import coop.stlma.tech.protocolsn.userplugin.service.UserGroupsService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.UUID;

@MicronautTest
class UserGroupsControllerTest {

    private static final UUID USER_ID = UUID.nameUUIDFromBytes("controllerGroupedUser".getBytes());
    private static final UUID GROUP_ID = UUID.nameUUIDFromBytes("controllerGroup".getBytes());

    @MockBean
    @Primary
    UserGroupsService userGroupsServiceMock = Mockito.mock(UserGroupsService.class);

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void testAddUserToGroup_happyPath() {

        Mockito.when(userGroupsServiceMock.addUserToGroup(USER_ID, GROUP_ID)).thenReturn(Mono.empty());

        HttpRequest<?> request = HttpRequest.PUT(
                UserGroupsOperations.ADD_USER_TO_GROUP_PATH
                        .replace("{userId}", USER_ID.toString())
                        .replace("{groupId}", GROUP_ID.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }

    @Test
    void testAddUserToGroup_noRoles() {

        HttpRequest<?> finalRequest = HttpRequest.PUT(
                        UserGroupsOperations.ADD_USER_TO_GROUP_PATH
                                .replace("{userId}", USER_ID.toString())
                                .replace("{groupId}", GROUP_ID.toString()), "")
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testRemoveUserFromGroup_happyPath() {

        Mockito.when(userGroupsServiceMock.removeUserFromGroup(USER_ID, GROUP_ID)).thenReturn(Mono.empty());

        HttpRequest<?> request = HttpRequest.DELETE(
                        UserGroupsOperations.REMOVE_USER_FROM_GROUP_PATH
                                .replace("{userId}", USER_ID.toString())
                                .replace("{groupId}", GROUP_ID.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }

    @Test
    void testRemoveUserFromGroup_noRoles() {

        HttpRequest<?> finalRequest = HttpRequest.DELETE(
                        UserGroupsOperations.ADD_USER_TO_GROUP_PATH
                                .replace("{userId}", USER_ID.toString())
                                .replace("{groupId}", GROUP_ID.toString()), "")
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

}
