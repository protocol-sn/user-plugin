package coop.stlma.tech.protocolsn.userplugin.error.handler;

import coop.stlma.tech.protocolsn.userplugin.error.UserLacksRoleException;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * Handle @link {UserLacksRoleException}
 *
 * @author John Meyerin
 */
@Singleton
public class UserLacksRoleExceptionHandler implements ExceptionHandler<UserLacksRoleException, HttpResponse> {
    @Override
    public HttpResponse handle(HttpRequest request, UserLacksRoleException exception) {
        return HttpResponse.status(HttpStatus.FORBIDDEN);
    }
}
