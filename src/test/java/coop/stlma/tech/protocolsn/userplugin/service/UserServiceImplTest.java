package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import coop.stlma.tech.protocolsn.registration.model.UserQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.TestUtil;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@MicronautTest
class UserServiceImplTest {

    private static final UUID USER_ID = UUID.nameUUIDFromBytes("someUser".getBytes());

    @MockBean
    @Primary
    KeycloakAdminClient keycloakApiMock = Mockito.mock(KeycloakAdminClient.class);

    @MockBean
    @Primary
    UserService userServiceMock = Mockito.mock(UserService.class);

    @Inject
    UserServiceImpl userVerificationService;

    ArgumentCaptor<UserRepresentation> userCaptor = ArgumentCaptor.forClass(UserRepresentation.class);

    @Test
    void testApproveUser_happyPath() {
        UserRepresentation returnedUser = new UserRepresentation();

        Mockito.when(keycloakApiMock.getUser("social-network-ecosystem",
                        USER_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(returnedUser)));

        Mockito.when(keycloakApiMock.updateUser(Mockito.eq("social-network-ecosystem"),
                Mockito.eq(USER_ID.toString()), Mockito.any(UserRepresentation.class)))
                .thenReturn(Mono.empty());

        userVerificationService.approveUser(USER_ID).block();

        Mockito.verify(keycloakApiMock).updateUser(
                Mockito.eq("social-network-ecosystem"),
                Mockito.eq(USER_ID.toString()),
                userCaptor.capture());

        UserRepresentation updatedUser = userCaptor.getValue();

        Assertions.assertTrue(Boolean.parseBoolean(updatedUser.getAttributes().get("approved").get(0)));
    }

    @Test
    void testQueryUsers_happyPath() {
        UserQueryCriteria query = UserQueryCriteria.builder()
                .limit(5)
                .offset(0)
                .search("-user")
                .build();

        List<UserRepresentation> expectedReps = List.of(
                TestUtil.buildUserRepresentation("user1"),
                TestUtil.buildUserRepresentation("user2"),
                TestUtil.buildUserRepresentation("user3"),
                TestUtil.buildUserRepresentation("user4"),
                TestUtil.buildUserRepresentation("user5")
        );

        Mockito.when(keycloakApiMock.queryUsers(Mockito.eq("social-network-ecosystem"), Mockito.isNull(), Mockito.isNull(),
                        Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.eq(0), Mockito.isNull(),
                        Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.eq(5), Mockito.eq(query.parseToQ()),
                        Mockito.eq("-user"), Mockito.isNull()))
                .thenReturn(Mono.just(HttpResponse.ok(expectedReps)));

        Mockito.when(keycloakApiMock.getUserGroups(
                Mockito.eq("social-network-ecosystem"), Mockito.any(), Mockito.eq(true), Mockito.eq(0),
                        Mockito.eq(25), Mockito.isNull()))
                .thenReturn(Mono.just(HttpResponse.ok(TestUtil.buildUserGroupRepresentations(2))));

        List<PsnUser> result = userVerificationService.queryUsers(query).collectList().block();

        Assertions.assertEquals(5, result.size());
        result = result.stream()
                .sorted(Comparator.comparing(PsnUser::getUsername))
                .toList();

        Assertions.assertEquals("user1", result.get(0).getUsername());
        Assertions.assertEquals("user2", result.get(1).getUsername());
        Assertions.assertEquals("user3", result.get(2).getUsername());
        Assertions.assertEquals("user4", result.get(3).getUsername());
        Assertions.assertEquals("user5", result.get(4).getUsername());

        Assertions.assertEquals(2, result.get(0).getGroupMembership().size());
        Assertions.assertEquals("TestGroup1", result.get(0).getGroupMembership().get(0).getGroupName());
        Assertions.assertEquals("TestGroup2", result.get(0).getGroupMembership().get(1).getGroupName());
    }

    @Test
    void testQueryUsers_emptyList() {
        UserQueryCriteria query = UserQueryCriteria.builder()
                .limit(5)
                .offset(0)
                .build();

        List<UserRepresentation> expectedReps = Collections.emptyList();

        Mockito.when(keycloakApiMock.queryUsers(Mockito.eq("social-network-ecosystem"), Mockito.isNull(), Mockito.isNull(),
                        Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.eq(0), Mockito.isNull(),
                        Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.eq(5), Mockito.eq(query.parseToQ()),
                        Mockito.isNull(), Mockito.isNull()))
                .thenReturn(Mono.just(HttpResponse.ok(expectedReps)));

        List<PsnUser> result = userVerificationService.queryUsers(query).collectList().block();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    void testQueryUsers_nullList() {
        UserQueryCriteria query = UserQueryCriteria.builder()
                .limit(5)
                .offset(0)
                .build();

        Mockito.when(keycloakApiMock.queryUsers(Mockito.eq("social-network-ecosystem"), Mockito.isNull(), Mockito.isNull(),
                        Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.eq(0), Mockito.isNull(),
                        Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.eq(5), Mockito.eq(query.parseToQ()),
                        Mockito.isNull(), Mockito.isNull()))
                .thenReturn(Mono.empty());

        List<PsnUser> result = userVerificationService.queryUsers(query).collectList().block();

        Assertions.assertEquals(0, result.size());
    }

    @Test
    void testGetUser_happyPath() {
        UserRepresentation user1 = TestUtil.buildUserRepresentation("user1");
        Mockito.when(keycloakApiMock.getUser("social-network-ecosystem", USER_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(user1)));
        Mockito.when(keycloakApiMock.getUserGroups("social-network-ecosystem", user1.getId(), true, 0, 25, null))
                .thenReturn(Mono.just(HttpResponse.ok(TestUtil.buildUserGroupRepresentations(2))));

        PsnUser result = userVerificationService.getUser(USER_ID).block();

        Assertions.assertEquals("user1", result.getUsername());
        Assertions.assertEquals(2, result.getGroupMembership().size());
        Assertions.assertEquals("TestGroup1", result.getGroupMembership().get(0).getGroupName());
        Assertions.assertEquals("TestGroup2", result.getGroupMembership().get(1).getGroupName());
    }
}
