package io.github.telegram.bot.prometheus;

import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


class TelegramOkHttpMetricsEventListenerTests {

    static MockWebServer mockWebServer;
    static final String TOKEN = "32jbksdjb324sadzxfdsax";
    static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    static final EventListener listener = TelegramOkHttpMetricsEventListener
            .builder()
            .registry(registry)
            .name("tg.http.requests")
            .build();
    static OkHttpClient okHttpClient;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        okHttpClient = new OkHttpClient.Builder()
                .eventListener(listener)
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody("""
                        {
                            "ok": true,
                            "result": {
                                "message_id": 17322,
                                "from": {
                                    "id": 123312412313,
                                    "is_bot": true,
                                    "first_name": "MyBot",
                                    "username": "MyBot"
                                },
                                "chat": {
                                    "id": 123456213213129,
                                    "first_name": "Test",
                                    "username": "test",
                                    "type": "private"
                                },
                                "date": 1765035838,
                                "text": "hi"
                            }
                        }
                        """));
    }

    @AfterAll
    static void afterAll() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @Test
    void requestTest() throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(mockWebServer.url("/bot" + TOKEN + "/sendMessage?chat_id=123456213213129&text=hi"))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            assertThat(registry.scrape()).contains("status=\"200\"");
            assertThat(registry.scrape()).contains("uri=\"/sendMessage\"");
        }
    }

    @Test
    void metricsWithoutToken() throws IOException {
        Request request = new Request.Builder()
                .get()
                .url(mockWebServer.url("/bot" + TOKEN + "/sendMessage?chat_id=123456213213129&text=hi"))
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            assertThat(registry.scrape()).doesNotContain(TOKEN);
            assertThat(registry.scrape()).doesNotContain("bot" + TOKEN);
        }
    }

    @Test
    void throwExIfRegistryIsNull() {
        assertThatThrownBy(() -> TelegramOkHttpMetricsEventListener
                .builder()
                .build()).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void throwExIfTryToCreateConst() throws NoSuchMethodException {
        Constructor<TelegramOkHttpMetricsEventListener> constructor = TelegramOkHttpMetricsEventListener.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasCauseInstanceOf(IllegalStateException.class);
    }

}
