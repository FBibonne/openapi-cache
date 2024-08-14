package fr.insee.oascache.metadata;

import fr.insee.oascache.metadata.api.model.Commune;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class GeoCommuneClientTest {

    @Autowired
    private GeoCommuneClient geoCommuneClient;

    static final MockWebServer server = new MockWebServer();
    static final String serverBaseUrl = server.url("/").toString();

    @DynamicPropertySource
    static void mockWebServerBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("spring.cloud.openfeign.client.config.metadata.url", () -> serverBaseUrl);
    }

    @Test
    void test() throws InterruptedException {
        enqueueServerResponse();
        Commune commune = geoCommuneClient.getcogcom("33529", (LocalDate) null);
        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).hasToString("/geo/commune/33529");
        assertThat(request.getMethod()).hasToString("GET");
        assertThat(commune.getIntitule()).hasToString("La Teste");
    }

    private void enqueueServerResponse() {
        var response = new MockResponse();
        response.setHeader("content-type", "application/json");
        response.setBody("{\"code\":\"33529\", \"intitule\":\"La Teste\"}");
        response.setResponseCode(200);
        server.enqueue(response);
    }

    @AfterAll
    static void tearDown() throws IOException {
        server.shutdown();
    }

}
