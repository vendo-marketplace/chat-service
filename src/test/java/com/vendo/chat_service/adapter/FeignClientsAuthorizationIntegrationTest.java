package com.vendo.chat_service.adapter;

import com.sun.net.httpserver.HttpServer;
import com.vendo.chat_service.adapter.product.out.ProductClient;
import com.vendo.chat_service.adapter.user.out.UserClient;
import com.vendo.core_lib.types.ServiceName;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class FeignClientsAuthorizationIntegrationTest {

    private static final HttpServer SERVER = startServer();
    private static final Map<String, List<String>> AUTHORIZATION_BY_PATH = new ConcurrentHashMap<>();

    @Autowired
    private UserClient userClient;
    @Autowired
    private ProductClient productClient;

    @DynamicPropertySource
    static void clientUrls(DynamicPropertyRegistry registry) {
        String url = "http://localhost:" + SERVER.getAddress().getPort();
        registry.add("spring.cloud.openfeign.client.config.user-service.url", () -> url);
        registry.add("spring.cloud.openfeign.client.config.product-service.url", () -> url);
    }

    @BeforeEach
    void clearRecordedRequests() {
        AUTHORIZATION_BY_PATH.clear();
    }

    @AfterAll
    static void stopServer() {
        SERVER.stop(0);
    }

    @Test
    void userClientSendsSingleTokenForUserService() {
        userClient.getById("user-id");

        List<String> headers = AUTHORIZATION_BY_PATH.get("/internal/users");
        assertThat(headers).singleElement()
                .satisfies(header -> assertThat(tokenPayloadOf(header))
                        .contains("\"" + ServiceName.USER_SERVICE.getServiceName() + "\"")
                        .doesNotContain(ServiceName.PRODUCT_SERVICE.getServiceName()));
    }

    @Test
    void productClientSendsSingleTokenForProductService() {
        productClient.findById("product-id");

        List<String> headers = AUTHORIZATION_BY_PATH.get("/internal/products/product-id");
        assertThat(headers).singleElement()
                .satisfies(header -> assertThat(tokenPayloadOf(header))
                        .contains("\"" + ServiceName.PRODUCT_SERVICE.getServiceName() + "\"")
                        .doesNotContain(ServiceName.USER_SERVICE.getServiceName()));
    }

    private static String tokenPayloadOf(String authorizationHeader) {
        assertThat(authorizationHeader).startsWith("Bearer ");
        String payload = authorizationHeader.substring("Bearer ".length()).split("\\.")[1];
        return new String(Base64.getUrlDecoder().decode(payload), StandardCharsets.UTF_8);
    }

    private static HttpServer startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/internal/users", exchange -> {
                AUTHORIZATION_BY_PATH.put(exchange.getRequestURI().getPath(), exchange.getRequestHeaders().getOrDefault("Authorization", List.of()));
                respond(exchange, "{\"id\":\"user-id\",\"email\":\"user@vendo.test\",\"emailVerified\":true,\"roles\":[\"USER\"],\"status\":\"ACTIVE\",\"fullName\":\"User Name\"}");
            });
            server.createContext("/internal/products", exchange -> {
                AUTHORIZATION_BY_PATH.put(exchange.getRequestURI().getPath(), exchange.getRequestHeaders().getOrDefault("Authorization", List.of()));
                respond(exchange, "{\"id\":\"product-id\",\"ownerId\":\"owner-id\"}");
            });
            server.start();
            return server;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

}
