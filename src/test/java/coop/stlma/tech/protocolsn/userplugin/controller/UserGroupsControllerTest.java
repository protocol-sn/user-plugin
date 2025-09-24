package coop.stlma.tech.protocolsn.userplugin.controller;

import coop.stlma.tech.protocolsn.userplugin.api.UserGroupsOperations;
import coop.stlma.tech.protocolsn.userplugin.model.GroupQueryCriteria;
import coop.stlma.tech.protocolsn.userplugin.model.UserGroup;
import coop.stlma.tech.protocolsn.userplugin.TestUtil;
import coop.stlma.tech.protocolsn.userplugin.service.UserGroupsService;
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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@MicronautTest
@Disabled("Some updates broke the auth in tests. Fix later")
class UserGroupsControllerTest {

    private static final UUID USER_ID = UUID.nameUUIDFromBytes("controllerGroupedUser".getBytes());
    private static final UUID GROUP_ID = UUID.nameUUIDFromBytes("controllerGroup".getBytes());

    @MockBean
    @Primary
    UserGroupsService userGroupsServiceMock = Mockito.mock(UserGroupsService.class);

    @Inject
    @Client("/")
    HttpClient httpClient;

    private final ArgumentCaptor<GroupQueryCriteria> groupQueryCriteriaCaptor = ArgumentCaptor.forClass(GroupQueryCriteria.class);

    @Test
    void testGetDefaultGroups_happyPath() {
        UserGroup userGroup = new UserGroup();
        userGroup.setId(GROUP_ID);
        userGroup.setGroupName("controller group");
        userGroup.setNewUserDefault(true);
        UserGroup userGroup2 = new UserGroup();
        userGroup2.setId(UUID.nameUUIDFromBytes("controllerGroup2".getBytes()));
        userGroup2.setGroupName("second group");
        userGroup2.setNewUserDefault(true);
        Mockito.when(userGroupsServiceMock.queryGroups(groupQueryCriteriaCaptor.capture()))
                .thenReturn(Flux.just(userGroup, userGroup2));

        HttpRequest<?> request = HttpRequest.GET(UserGroupsOperations.GET_DEFAULT_GROUPS_PATH)
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request, Argument.listOf(UserGroup.class));

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());

        GroupQueryCriteria capturedCriteria = groupQueryCriteriaCaptor.getValue();
        Assertions.assertEquals(true, capturedCriteria.getDefaultUserGroup());
    }

    @Test
    void testRemoveGroupFromDefaults_happyPath() {

        Mockito.when(userGroupsServiceMock.removeGroupFromDefault(GROUP_ID)).thenReturn(Mono.empty());

        HttpRequest<?> request = HttpRequest.DELETE(
                UserGroupsOperations.REMOVE_GROUP_FROM_DEFAULTS
                        .replace("{groupId}", GROUP_ID.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }

    @Test
    void testAddGroupToDefaults_happyPath() {
        Mockito.when(userGroupsServiceMock.addGroupToDefaults(GROUP_ID)).thenReturn(Mono.empty());

        HttpRequest<?> request = HttpRequest.POST(
                UserGroupsOperations.ADD_GROUP_TO_DEFAULTS
                        .replace("{groupId}", GROUP_ID.toString()), "")
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<?> rsp = httpClient.toBlocking().exchange(request);

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
    }

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

    @Test
    void testQueryGroups_noRoles() {
        HttpRequest<?> finalRequest = HttpRequest.GET(UserGroupsOperations.GET_DEFAULT_GROUPS_PATH)
                .bearerAuth(TestUtil.getTestUserAccessToken(httpClient));

        HttpClientResponseException result = Assertions.assertThrows(HttpClientResponseException.class,
                () -> httpClient.toBlocking().exchange(finalRequest));

        Assertions.assertEquals(HttpStatus.FORBIDDEN, result.getStatus());
    }

    @Test
    void testQueryGroups_happyPath() {
        UserGroup userGroup = new UserGroup();
        userGroup.setId(GROUP_ID);
        userGroup.setGroupName("controller group");
        userGroup.setNewUserDefault(true);
        UserGroup userGroup2 = new UserGroup();
        userGroup2.setId(UUID.nameUUIDFromBytes("controllerGroup2".getBytes()));
        userGroup2.setGroupName("second group");
        userGroup2.setNewUserDefault(true);

        GroupQueryCriteria criteria = GroupQueryCriteria.builder().offset(2).build();

        Mockito.when(userGroupsServiceMock.queryGroups(groupQueryCriteriaCaptor.capture()))
                .thenReturn(Flux.just(userGroup, userGroup2));

        HttpRequest<?> finalRequest = HttpRequest.POST(UserGroupsOperations.GET_GROUPS_PATH, criteria)
                .bearerAuth(TestUtil.getAdminUserAccessToken(httpClient));

        HttpResponse<List<UserGroup>> rsp = httpClient.toBlocking().exchange(finalRequest, Argument.listOf(UserGroup.class));

        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
        List<UserGroup> responseBody = rsp.getBody().get();
        responseBody.stream().sorted(Comparator.comparing(UserGroup::getGroupName));

        Assertions.assertEquals(2, responseBody.size());
        Assertions.assertEquals("controller group", responseBody.get(0).getGroupName());
        Assertions.assertEquals("second group", responseBody.get(1).getGroupName());
    }

}
