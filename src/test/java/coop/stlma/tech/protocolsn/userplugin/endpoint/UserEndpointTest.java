package coop.stlma.tech.protocolsn.userplugin.endpoint;

import coop.stlma.tech.protocolsn.userplugin.Pagination;
import coop.stlma.tech.protocolsn.userplugin.SuccessResponse;
import coop.stlma.tech.protocolsn.userplugin.UserData;
import coop.stlma.tech.protocolsn.userplugin.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.UserReference;
import coop.stlma.tech.protocolsn.userplugin.UserSearchResponse;
import coop.stlma.tech.protocolsn.userplugin.UsersServiceGrpc;
import coop.stlma.tech.protocolsn.userplugin.model.PsnUser;
import coop.stlma.tech.protocolsn.userplugin.service.UserService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@MicronautTest
class UserEndpointTest {

    @Inject
    UsersServiceGrpc.UsersServiceBlockingStub usersServiceBlockingStub;

    @MockBean
    @Primary
    UserService userServiceMock = Mockito.mock(UserService.class);

    UUID userId = UUID.nameUUIDFromBytes("MyUser".getBytes());

    @Test
    void testApproveUser_error() {
        UserReference userReference = UserReference.newBuilder().setUserId(userId.toString()).build();
        Mockito.when(userServiceMock.approveUser(userId)).thenReturn(Mono.error(new RuntimeException("Test exception")));

        SuccessResponse result = usersServiceBlockingStub.approveUser(userReference);

        Assertions.assertFalse(result.getSuccess());
    }

    @Test
    void testApproveUser_happyPath() {
        UserReference userReference = UserReference.newBuilder().setUserId(userId.toString()).build();
        Mockito.when(userServiceMock.approveUser(userId)).thenReturn(Mono.empty());

        SuccessResponse result = usersServiceBlockingStub.approveUser(userReference);

        Assertions.assertTrue(result.getSuccess());

    }

    @Test
    void testQueryUsers_happyPath() {
        UserQueryCriteria userQueryCriteria = UserQueryCriteria.newBuilder()
                .setPagination(Pagination.newBuilder()
                        .setLimit(5)
                        .setOffset(9)
                        .build())
                .setApproved(true)
                .build();

        List<PsnUser> returnedUsers = List.of(
                PsnUser.builder().id("one").username("user1").build(),
                PsnUser.builder().id("two").username("user2").build(),
                PsnUser.builder().id("three").username("user3").build()
        );
        Mockito.when(userServiceMock.queryUsers(9, 5, true, null, null, null))
                .thenReturn(Flux.fromIterable(returnedUsers));

        UserSearchResponse result = usersServiceBlockingStub.queryUsers(userQueryCriteria);

        Assertions.assertEquals(3, result.getUsersCount());
        Assertions.assertEquals(3, result.getUsersList().size());

        List<UserData> responseBody = result.getUsersList().stream()
                .sorted(Comparator.comparing(UserData::getUsername)).toList();

        Assertions.assertEquals("user1", responseBody.get(0).getUsername());
        Assertions.assertEquals("user2", responseBody.get(1).getUsername());
        Assertions.assertEquals("user3", responseBody.get(2).getUsername());
    }

    @Test
    void testQueryUsers_oneFound() {
        UserQueryCriteria userQueryCriteria = UserQueryCriteria.newBuilder()
                .setPagination(Pagination.newBuilder().setLimit(5).setOffset(9).build())
                .setApproved(true)
                .build();

        List<PsnUser> returnedUsers = List.of(
                PsnUser.builder().id("one").username("user1").build()
        );

        Mockito.when(userServiceMock.queryUsers(9, 5, true, null, null, null))
                .thenReturn(Flux.fromIterable(returnedUsers));

        UserSearchResponse result = usersServiceBlockingStub.queryUsers(userQueryCriteria);

        Assertions.assertEquals(1, result.getUsersCount());
        Assertions.assertEquals(1, result.getUsersList().size());
        Assertions.assertEquals("one", result.getUsersList().get(0).getId());
    }

    @Test
    void testQueryUsers_noneFound() {

        UserQueryCriteria userQueryCriteria = UserQueryCriteria.newBuilder()
                .setPagination(Pagination.newBuilder().setOffset(0).setLimit(10).build())
                .build();

        Mockito.when(userServiceMock.queryUsers(0, 10, null, null, null, null))
                .thenReturn(Flux.empty());

        UserSearchResponse result = usersServiceBlockingStub.queryUsers(userQueryCriteria);

        Assertions.assertEquals(0, result.getUsersCount());
        Assertions.assertEquals(0, result.getUsersList().size());
    }

    @Test
    void testGetUsers_happyPath() {
        UserReference userReference = UserReference.newBuilder().setUserId(userId.toString()).build();

        Mockito.when(userServiceMock.getUser(userId))
                .thenReturn(Mono.just(PsnUser.builder().id(userId.toString()).username("user1").build()));

        UserData result = usersServiceBlockingStub.getUser(userReference);

        Assertions.assertEquals(userId.toString(), result.getId());
        Assertions.assertEquals("user1", result.getUsername());
    }
}
