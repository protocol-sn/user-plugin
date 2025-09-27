package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.userplugin.api.UserOperations;
import coop.stlma.tech.protocolsn.userplugin.model.PsnUser;
import coop.stlma.tech.protocolsn.userplugin.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.TestUtil;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.type.Argument;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static coop.stlma.tech.protocolsn.userplugin.AuthProviderCreds.TEST_USER_ID;

@MicronautTest
class UserControllerTest {

    public static UUID userId = UUID.nameUUIDFromBytes("controllerUserId".getBytes());

    @MockBean
    @Primary
    UserService userServiceMock = Mockito.mock(UserService.class);

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void testApproveUser_happyPath() {
        Mockito.when(userServiceMock.approveUser(userId)).thenReturn(Mono.empty());

        HttpRequest<?> request = HttpRequest.PUT(UserOperations.APPROVE_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }

    @Test
    void testApproveUser_noRoles() {
        HttpRequest<?> finalRequest = HttpRequest.PUT(UserOperations.APPROVE_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testQueryUsers_happyPath() {
        UserQueryCriteria searchCriteria = UserQueryCriteria.builder()
                .limit(5)
                .offset(9)
                .approved(true)
                .build();

        ArgumentCaptor<UserQueryCriteria> captor = ArgumentCaptor.forClass(UserQueryCriteria.class);
        List<PsnUser> returnedUsers = List.of(
                PsnUser.builder().id("one").username("user1").build(),
                PsnUser.builder().id("two").username("user2").build(),
                PsnUser.builder().id("three").username("user3").build()
        );
        Mockito.when(userServiceMock.queryUsers(captor.capture())).thenReturn(Flux.fromIterable(returnedUsers));

        HttpRequest<?> request = HttpRequest.POST(UserOperations.QUERY_PATH, searchCriteria)
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertTrue(capturedCriteria.getApproved());
        Assertions.assertEquals(5, capturedCriteria.getLimit());
        Assertions.assertEquals(9, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, queryResponse.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(3, responseBody.size());
        responseBody = responseBody.stream()
                .sorted(Comparator.comparing(PsnUser::getUsername)).toList();

        Assertions.assertEquals("user1", responseBody.get(0).getUsername());
        Assertions.assertEquals("user2", responseBody.get(1).getUsername());
        Assertions.assertEquals("user3", responseBody.get(2).getUsername());
    }

    @Test
    void testQueryUsers_oneFound() {
        UserQueryCriteria searchCritera = UserQueryCriteria.builder()
                .limit(5)
                .offset(9)
                .approved(true)
                .build();

        ArgumentCaptor<UserQueryCriteria> captor = ArgumentCaptor.forClass(UserQueryCriteria.class);
        List<PsnUser> returnedUsers = List.of(
                PsnUser.builder().id("one").username("user1").build()
        );
        Mockito.when(userServiceMock.queryUsers(captor.capture())).thenReturn(Flux.fromIterable(returnedUsers));

        HttpRequest<?> request = HttpRequest.POST(UserOperations.QUERY_PATH, searchCritera)
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertTrue(capturedCriteria.getApproved());
        Assertions.assertEquals(5, capturedCriteria.getLimit());
        Assertions.assertEquals(9, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, queryResponse.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(1, responseBody.size());

        Assertions.assertEquals("user1", responseBody.get(0).getUsername());
    }

    @Test
    void testQueryUsers_noneFound() {
        UserQueryCriteria searchCritera = UserQueryCriteria.builder()
                .limit(5)
                .offset(9)
                .approved(true)
                .build();

        ArgumentCaptor<UserQueryCriteria> captor = ArgumentCaptor.forClass(UserQueryCriteria.class);
        Mockito.when(userServiceMock.queryUsers(captor.capture())).thenReturn(Flux.empty());

        HttpRequest<?> request = HttpRequest.POST(UserOperations.QUERY_PATH, searchCritera)
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertTrue(capturedCriteria.getApproved());
        Assertions.assertEquals(5, capturedCriteria.getLimit());
        Assertions.assertEquals(9, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, queryResponse.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(0, responseBody.size());
    }

    @Test
    void testQueryUsers_noRoles() {
        HttpRequest<?> finalRequest = HttpRequest.PUT(UserOperations.QUERY_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testPendingApproval_happyPath() {

        ArgumentCaptor<UserQueryCriteria> captor = ArgumentCaptor.forClass(UserQueryCriteria.class);
        List<PsnUser> returnedUsers = List.of(
                PsnUser.builder().id("one").username("user1").build(),
                PsnUser.builder().id("two").username("user2").build(),
                PsnUser.builder().id("three").username("user3").build()
        );
        Mockito.when(userServiceMock.queryUsers(captor.capture())).thenReturn(Flux.fromIterable(returnedUsers));

        HttpRequest<?> request = HttpRequest.GET(UserOperations.PENDING_APPROVAL_PATH)
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertFalse(capturedCriteria.getApproved());
        Assertions.assertEquals(25, capturedCriteria.getLimit());
        Assertions.assertEquals(0, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, queryResponse.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(3, responseBody.size());
        responseBody = responseBody.stream()
                .sorted(Comparator.comparing(PsnUser::getUsername)).toList();

        Assertions.assertEquals("user1", responseBody.get(0).getUsername());
        Assertions.assertEquals("user2", responseBody.get(1).getUsername());
        Assertions.assertEquals("user3", responseBody.get(2).getUsername());
    }

    @Test
    void testPendingApproval_noRoles() {
        HttpRequest<?> finalRequest = HttpRequest.PUT(UserOperations.PENDING_APPROVAL_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testGetUser_happyPath() {
        Mockito.when(userServiceMock.getUser(userId))
                .thenReturn(
                        Mono.just(PsnUser.builder().id("one").username("user1").build()));

        HttpRequest<?> finalRequest = HttpRequest.GET(UserOperations.GET_USER_PATH.replace("{userId}", userId.toString()))
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<PsnUser> result = httpClient.toBlocking().exchange(finalRequest, PsnUser.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatus());
        Assertions.assertEquals("user1", result.getBody().get().getUsername());
    }

    @Test
    void testGetUser_nonAdminCannotGetAnotherUser() {

        HttpRequest<?> finalRequest = HttpRequest.GET(UserOperations.GET_USER_PATH.replace("{userId}", userId.toString()))
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class, () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testGetUser_nonAdminCanGetSelf() {
        Mockito.when(userServiceMock.getUser(TEST_USER_ID))
                .thenReturn(
                        Mono.just(PsnUser.builder().id("one").username("user1").build()));

        HttpRequest<?> finalRequest = HttpRequest.GET(UserOperations.GET_USER_PATH.replace("{userId}", TEST_USER_ID.toString()))
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpResponse<PsnUser> result = httpClient.toBlocking().exchange(finalRequest, PsnUser.class);

        Assertions.assertEquals(HttpStatus.OK, result.getStatus());
        Assertions.assertEquals("user1", result.getBody().get().getUsername());
    }
}
