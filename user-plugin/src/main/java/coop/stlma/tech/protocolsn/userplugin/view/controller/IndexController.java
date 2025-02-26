package coop.stlma.tech.protocolsn.userplugin.view.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller("/")
public class IndexController {

    @Produces(MediaType.TEXT_HTML)
    @View("user")
    @Get
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<Map<String, String>> homePage() {
        Map<String, String> attributes = new HashMap<>();
        return HttpResponse.ok(attributes);
    }

//    @Produces(MediaType.TEXT_HTML)
//    @Get("/")
//    @Secured(SecurityRule.IS_AUTHENTICATED)
//    public HttpResponse<Map<String, String>> userPage(Optional<Authentication> authentication) {
//        Map<String, String> attributes = new HashMap<>();
//        if (authentication.isPresent()) {
//            if (authentication.get().getAttributes().containsKey("user-moderator")) {
//                attributes.put("userModerator", "true");
//            }
//        }
//        return HttpResponse.ok(attributes);
//    }
}
