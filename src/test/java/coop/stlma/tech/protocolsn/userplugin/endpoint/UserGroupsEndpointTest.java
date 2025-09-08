package coop.stlma.tech.protocolsn.userplugin.endpoint;

import coop.stlma.tech.protocolsn.userplugin.GroupReference;
import coop.stlma.tech.protocolsn.userplugin.GroupSearchResponse;
import coop.stlma.tech.protocolsn.userplugin.Pagination;
import coop.stlma.tech.protocolsn.userplugin.SuccessResponse;
import coop.stlma.tech.protocolsn.userplugin.UserGroupData;
import coop.stlma.tech.protocolsn.userplugin.UserGroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.UserGroupReference;
import coop.stlma.tech.protocolsn.userplugin.UserGroupsServiceGrpc;
import coop.stlma.tech.protocolsn.userplugin.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.model.UserGroup;
import coop.stlma.tech.protocolsn.userplugin.service.UserGroupsService;
import io.micronaut.context.annotation.Primary;
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
class UserGroupsEndpointTest {

    private static final UUID USER_ID = UUID.nameUUIDFromBytes("controllerGroupedUser".getBytes());
    private static final UUID GROUP_ID = UUID.nameUUIDFromBytes("controllerGroup".getBytes());

    @MockBean
    @Primary
    UserGroupsService userGroupsServiceMock = Mockito.mock(UserGroupsService.class);

    @Inject
    UserGroupsServiceGrpc.UserGroupsServiceBlockingStub userGroupsServiceBlockingStub;

    private final ArgumentCaptor<GroupQueryCriteria> groupQueryCriteriaCaptor = ArgumentCaptor.forClass(GroupQueryCriteria.class);

    @Test
    void testRemoveGroupFromDefaults_happyPath() {
        UserGroupReference userGroupReference = UserGroupReference.newBuilder()
                .setGroupId(GROUP_ID.toString())
                .setUserId(USER_ID.toString())
                .build();

        Mockito.when(userGroupsServiceMock.removeUserFromGroup(USER_ID, GROUP_ID)).thenReturn(Mono.empty());

        SuccessResponse successResponse = userGroupsServiceBlockingStub.removeUserFromGroup(userGroupReference);

        Assertions.assertTrue(successResponse.getSuccess());

    }

    @Test
    void testAddGroupToDefaults_happyPath() {
        GroupReference groupReference = GroupReference.newBuilder()
                .setGroupId(GROUP_ID.toString())
                .build();

        Mockito.when(userGroupsServiceMock.addGroupToDefaults(GROUP_ID)).thenReturn(Mono.empty());

        SuccessResponse successResponse = userGroupsServiceBlockingStub.addGroupToDefaults(groupReference);

        Assertions.assertTrue(successResponse.getSuccess());
    }

    @Test
    void testAddUserToGroup_happyPath() {
        UserGroupReference userGroupReference = UserGroupReference.newBuilder()
                .setGroupId(GROUP_ID.toString())
                .setUserId(USER_ID.toString())
                .build();

        Mockito.when(userGroupsServiceMock.addUserToGroup(USER_ID, GROUP_ID)).thenReturn(Mono.empty());

        SuccessResponse successResponse = userGroupsServiceBlockingStub.addUserToGroup(userGroupReference);
    }


    @Test
    void testRemoveUserFromGroup_happyPath() {
        UserGroupReference userGroupReference = UserGroupReference.newBuilder()
                .setGroupId(GROUP_ID.toString())
                .setUserId(USER_ID.toString())
                .build();

        Mockito.when(userGroupsServiceMock.removeUserFromGroup(USER_ID, GROUP_ID)).thenReturn(Mono.empty());

        SuccessResponse successResponse = userGroupsServiceBlockingStub.removeUserFromGroup(userGroupReference);
    }

    @Test
    void testQueryGroups_happyPath() {
        UserGroupQueryCriteria userGroupQueryCriteria = UserGroupQueryCriteria.newBuilder()
                .setPagination(Pagination.newBuilder().setOffset(2).build())
                .build();

        UserGroup userGroup = new UserGroup();
        userGroup.setId(GROUP_ID);
        userGroup.setGroupName("controller group");
        userGroup.setNewUserDefault(true);
        UserGroup userGroup2 = new UserGroup();
        userGroup2.setId(UUID.nameUUIDFromBytes("controllerGroup2".getBytes()));
        userGroup2.setGroupName("second group");
        userGroup2.setNewUserDefault(true);


        Mockito.when(userGroupsServiceMock.queryGroups(Mockito.any(GroupQueryCriteria.class)))
                .thenReturn(Flux.just(userGroup, userGroup2));

        GroupSearchResponse groupSearchResponse = userGroupsServiceBlockingStub.getGroups(userGroupQueryCriteria);

        Assertions.assertEquals(2, groupSearchResponse.getUserGroupsCount());
        List<UserGroupData> responseBody = groupSearchResponse.getUserGroupsList();
        responseBody.stream().sorted(Comparator.comparing(UserGroupData::getGroupName));

        Assertions.assertEquals("controller group", groupSearchResponse.getUserGroups(0).getGroupName());
        Assertions.assertEquals("second group", groupSearchResponse.getUserGroups(1).getGroupName());
    }
}
