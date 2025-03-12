package coop.stlma.tech.protocolsn.userplugin;

import coop.stlma.tech.protocolsn.keycloak.domain.GroupRepresentation;
import coop.stlma.tech.protocolsn.keycloak.domain.UserRepresentation;
import coop.stlma.tech.protocolsn.registration.model.PsnUser;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.security.authentication.UsernamePasswordCredentials;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtil {

    public static List<PsnUser> buildPsnUsers(int numberToBuild) {
        List<PsnUser> returnMe = new ArrayList<>();
        for (int i = 1; i <= numberToBuild; i++) {
            returnMe.add(buildPsnUser("TestUser" + i));
        }
        return returnMe;
    }

    public static PsnUser buildPsnUser(String username) {
        return PsnUser.builder()
                .id(UUID.nameUUIDFromBytes(username.getBytes()).toString())
                .username(username)
                .email(username + "@protocolSN.com")
                .givenName(username.substring(0, username.length() / 2))
                .familyName(username.substring(username.length() / 2))
                .approved(true)
                .verified(true)
                .requestsVerification(true)
                .build();
    }

    public static UserRepresentation buildUserRepresentation(String username) {
        UserRepresentation returnMe = new UserRepresentation();
        returnMe.setUsername(username);
        return returnMe;
    }

    public static String getTestUserAccessToken(HttpClient httpClient, String userName, String password) {

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);
        HttpRequest<?> request = HttpRequest.POST("/login", creds);

        HttpResponse<BearerAccessRefreshToken> rsp = httpClient.toBlocking().exchange(request, BearerAccessRefreshToken.class);
        Assertions.assertEquals(HttpStatus.OK, rsp.getStatus());
        return rsp.body().getAccessToken();
    }

    public static String getAdminUserAccessToken(HttpClient httpClient) {
        return getTestUserAccessToken(httpClient, "AdminUser", "AdminPass");
    }

    public static String getTestUserAccessToken(HttpClient httpClient) {
        return getTestUserAccessToken(httpClient, "TestUser", "TestPass");
    }

    public static List<GroupRepresentation> buildUserGroupRepresentations(int numberToBuild) {
        List<GroupRepresentation> returnMe = new ArrayList<>();
        for (int i = 1; i <= numberToBuild; i++) {
            returnMe.add(buildUserGroupRepresentation("TestGroup" + i));
        }
        return returnMe;
    }

    private static GroupRepresentation buildUserGroupRepresentation(String groupName) {
        GroupRepresentation returnMe = new GroupRepresentation();
        returnMe.setId(UUID.nameUUIDFromBytes(groupName.getBytes()).toString());
        returnMe.setName(groupName);
        return returnMe;
    }
}
