package coop.stlma.tech.protocolsn.pluginlib.controller;

import coop.stlma.tech.protocolsn.health.model.HealthResponse;
import coop.stlma.tech.protocolsn.health.model.HealthStatus;
import coop.stlma.tech.protocolsn.pluginlib.health.service.HealthService;
import io.micronaut.context.annotation.Primary;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

@MicronautTest
class HealthControllerTest {

    @MockBean
    @Primary
    HealthService healthServiceMock = Mockito.mock(HealthService.class);

    @Inject
    @Client("/")
    HttpClient httpClient;

    @Test
    void testRegisterPlugin_happyPath() {
        HealthResponse expected = new HealthResponse(HealthStatus.HEALTHY, "All Good");
        Mockito.when(healthServiceMock.getPluginHealth())
                .thenReturn(Mono.just(expected));

        HttpResponse<HealthResponse> response = httpClient.toBlocking()
                .exchange(HttpRequest
                        .GET("/general/v0.1.0/health"), HealthResponse.class);

        HealthResponse healthResponse = response.getBody(HealthResponse.class).get();
        Assertions.assertNotNull(healthResponse);
        Assertions.assertEquals(HealthStatus.HEALTHY, healthResponse.getHealthStatus());
        Assertions.assertEquals("All Good", healthResponse.getDescription());
    }
}
