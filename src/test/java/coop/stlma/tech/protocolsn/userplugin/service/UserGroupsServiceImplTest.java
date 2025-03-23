package coop.stlma.tech.protocolsn.userplugin.service;

import coop.stlma.tech.protocolsn.keycloak.client.KeycloakAdminClient;
import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;
import coop.stlma.tech.protocolsn.registration.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.registration.model.UserGroup;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpResponse;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@MicronautTest
class UserGroupsServiceImplTest {
    private static final UUID USER_ID = UUID.nameUUIDFromBytes("groupedUser".getBytes());
    private static final UUID GROUP_ID = UUID.nameUUIDFromBytes("someGroup".getBytes());

    @MockBean
    @Primary
    KeycloakAdminClient keycloakApiMock = Mockito.mock(KeycloakAdminClient.class);

    @Inject
    UserGroupsServiceImpl userService;

    @Test
    void testQueryGroups_happyPath() {
        GroupRepresentation group = new GroupRepresentation();
        group.setId(GROUP_ID.toString());
        group.setName("some group");
        GroupRepresentation group2 = new GroupRepresentation();
        group2.setId(UUID.randomUUID().toString());
        group2.setName("some other group");

        Mockito.when(keycloakApiMock.queryGroups("social-network-ecosystem", null, null,
                        null, null, null, "default:true", null))
                .thenReturn(Mono.just(HttpResponse.ok(List.of(group, group2))));

        List<UserGroup> groups = userService.queryGroups(GroupQueryCriteria.builder()
                        .defaultUserGroup(true)
                .build()).collectList().block();

        Assertions.assertNotNull(groups);
        Assertions.assertEquals(2, groups.size());
        groups = groups.stream().sorted(Comparator.comparing(UserGroup::getGroupName)).toList();
        Assertions.assertEquals("some group", groups.get(0).getGroupName());
        Assertions.assertEquals("some other group", groups.get(1).getGroupName());
    }

    @Test
    void testRemoveGroupFromDefaults_happyPath() {
        GroupRepresentation expectedGroup = new GroupRepresentation();
        expectedGroup.setName("some group");
        Mockito.when(keycloakApiMock.getGroup("social-network-ecosystem", GROUP_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(expectedGroup)));
        Mockito.when(keycloakApiMock.updateGroup(Mockito.eq("social-network-ecosystem"), Mockito.eq(GROUP_ID.toString()), Mockito.any()))
                .thenReturn(Mono.empty());

        userService.removeGroupFromDefault(GROUP_ID).block();

        Mockito.verify(keycloakApiMock).updateGroup(Mockito.eq("social-network-ecosystem"), Mockito.eq(GROUP_ID.toString()), Mockito.any());
    }

    @Test
    void testAddGroupToDefaults_happyPath() {
        GroupRepresentation expectedGroup = new GroupRepresentation();
        expectedGroup.setName("some group");
        Mockito.when(keycloakApiMock.getGroup("social-network-ecosystem", GROUP_ID.toString()))
                .thenReturn(Mono.just(HttpResponse.ok(expectedGroup)));

        Mockito.when(keycloakApiMock.updateGroup(Mockito.eq("social-network-ecosystem"), Mockito.eq(GROUP_ID.toString()), Mockito.any()))
                .thenReturn(Mono.empty());

        userService.addGroupToDefaults(GROUP_ID).block();

        Mockito.verify(keycloakApiMock).updateGroup(Mockito.eq("social-network-ecosystem"), Mockito.eq(GROUP_ID.toString()), Mockito.any());
    }

    @Test
    void testAddUserToGroup_happyPath() {
        Mockito.when(keycloakApiMock.addUserToGroup("social-network-ecosystem", USER_ID.toString(), GROUP_ID.toString()))
                .thenReturn(Mono.empty());
        
        userService.addUserToGroup(USER_ID, GROUP_ID).block();

        Mockito.verify(keycloakApiMock).addUserToGroup("social-network-ecosystem", USER_ID.toString(), GROUP_ID.toString());
    }

    @Test
    void testRemoveUserFromGroup_happyPath() {
        Mockito.when(keycloakApiMock.removeUserFromGroup("social-network-ecosystem", USER_ID.toString(), GROUP_ID.toString()))
                .thenReturn(Mono.empty());

        userService.removeUserFromGroup(USER_ID, GROUP_ID).block();

        Mockito.verify(keycloakApiMock).removeUserFromGroup("social-network-ecosystem", USER_ID.toString(), GROUP_ID.toString());
    }
}
