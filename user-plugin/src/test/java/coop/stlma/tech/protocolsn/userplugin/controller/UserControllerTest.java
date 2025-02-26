package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.registration.api.UserOperations;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.type.Argument;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

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

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("AdminUser", "AdminPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        request = HttpRequest.PUT(UserOperations.APPROVE_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(rsp.body().getAccessToken());

        rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }

    @Test
    void testApproveUser_noRoles() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("TestUser", "TestPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        HttpRequest<?> finalRequest = HttpRequest.PUT(UserOperations.APPROVE_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(rsp.body().getAccessToken());

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testQueryUsers_happyPath() {
        UserQueryCriteria searchCritera = UserQueryCriteria.builder()
                .limit(5)
                .offset(9)
                .approved(true)
                .build();

        ArgumentCaptor<UserQueryCriteria> captor = ArgumentCaptor.forClass(UserQueryCriteria.class);
        List<PsnUser> returnedUsers = List.of(
                new PsnUser("one", "user1", null, null, null, true),
                new PsnUser("two", "user2", null, null, null, true),
                new PsnUser("three", "user3", null, null, null, true)
        );
        Mockito.when(userServiceMock.queryUsers(captor.capture())).thenReturn(Flux.fromIterable(returnedUsers));

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("AdminUser", "AdminPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        request = HttpRequest.POST(UserOperations.QUERY_PATH, searchCritera)
                .bearerAuth(rsp.body().getAccessToken());

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertTrue(capturedCriteria.getApproved());
        Assertions.assertEquals(5, capturedCriteria.getLimit());
        Assertions.assertEquals(9, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(3, responseBody.size());
        responseBody = responseBody.stream()
                .sorted(Comparator.comparing(PsnUser::username)).toList();

        Assertions.assertEquals("user1", responseBody.get(0).username());
        Assertions.assertEquals("user2", responseBody.get(1).username());
        Assertions.assertEquals("user3", responseBody.get(2).username());
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
                new PsnUser("one", "user1", null, null, null, true)
        );
        Mockito.when(userServiceMock.queryUsers(captor.capture())).thenReturn(Flux.fromIterable(returnedUsers));

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("AdminUser", "AdminPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        request = HttpRequest.POST(UserOperations.QUERY_PATH, searchCritera)
                .bearerAuth(rsp.body().getAccessToken());

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertTrue(capturedCriteria.getApproved());
        Assertions.assertEquals(5, capturedCriteria.getLimit());
        Assertions.assertEquals(9, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(1, responseBody.size());

        Assertions.assertEquals("user1", responseBody.get(0).username());
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

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("AdminUser", "AdminPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        request = HttpRequest.POST(UserOperations.QUERY_PATH, searchCritera)
                .bearerAuth(rsp.body().getAccessToken());

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertTrue(capturedCriteria.getApproved());
        Assertions.assertEquals(5, capturedCriteria.getLimit());
        Assertions.assertEquals(9, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(0, responseBody.size());
    }

    @Test
    void testQueryUsers_noRoles() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("TestUser", "TestPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        HttpRequest<?> finalRequest = HttpRequest.PUT(UserOperations.QUERY_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(rsp.body().getAccessToken());

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testPendingApproval_happyPath() {

        ArgumentCaptor<UserQueryCriteria> captor = ArgumentCaptor.forClass(UserQueryCriteria.class);
        List<PsnUser> returnedUsers = List.of(
                new PsnUser("one", "user1", null, null, null, true),
                new PsnUser("two", "user2", null, null, null, true),
                new PsnUser("three", "user3", null, null, null, true)
        );
        Mockito.when(userServiceMock.queryUsers(captor.capture())).thenReturn(Flux.fromIterable(returnedUsers));

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("AdminUser", "AdminPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        request = HttpRequest.GET(UserOperations.PENDING_APPROVAL_PATH)
                .bearerAuth(rsp.body().getAccessToken());

        HttpResponse<List<PsnUser>> queryResponse = httpClient.toBlocking().exchange(request);

        UserQueryCriteria capturedCriteria = captor.getValue();
        Assertions.assertFalse(capturedCriteria.getApproved());
        Assertions.assertEquals(25, capturedCriteria.getLimit());
        Assertions.assertEquals(0, capturedCriteria.getOffset());

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
        List<PsnUser> responseBody  = queryResponse.getBody(Argument.listOf(PsnUser.class)).get();
        Assertions.assertEquals(3, responseBody.size());
        responseBody = responseBody.stream()
                .sorted(Comparator.comparing(PsnUser::username)).toList();

        Assertions.assertEquals("user1", responseBody.get(0).username());
        Assertions.assertEquals("user2", responseBody.get(1).username());
        Assertions.assertEquals("user3", responseBody.get(2).username());
    }

    @Test
    void testPendingApproval_noRoles() {
        UsernamePasswordCredentials creds = new UsernamePasswordCredentials("TestUser", "TestPass");
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        HttpRequest<?> finalRequest = HttpRequest.PUT(UserOperations.PENDING_APPROVAL_PATH.replace("{userId}", userId.toString()), "")
                .bearerAuth(rsp.body().getAccessToken());

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }
}
