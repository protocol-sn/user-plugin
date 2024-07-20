package coop.stlma.tech.protocolsn.userplugin.view.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.HashMap;
import java.util.Map;

@Controller("/")
public class IndexController {

    @Produces(MediaType.TEXT_HTML)
    @View("user")
    @Get
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<Map<String, String>> homePage() {
        return HttpResponse.ok(new HashMap<>());
    }
}
