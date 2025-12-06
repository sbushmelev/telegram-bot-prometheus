package io.github.telegram.bot.prometheus;

import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import okhttp3.EventListener;

public class TelegramOkHttpMetricsEventListenerBuilder {

    private TelegramOkHttpMetricsEventListenerBuilder() {
        throw new IllegalStateException("This is builder class");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private PrometheusMeterRegistry registry;

        private String name = "http.request";

        private String botTag;

        public Builder registry(final PrometheusMeterRegistry registry) {
            this.registry = registry;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder botTag(final String botTag) {
            this.botTag = botTag;
            return this;
        }

        public EventListener build() {
            if (registry == null) {
                throw new IllegalStateException("Registry must be set");
            }

            OkHttpMetricsEventListener.Builder builder = OkHttpMetricsEventListener
                    .builder(registry, name);

            if (botTag != null) {
                builder.tag(Tag.of("bot", botTag));
            }

            return builder
                    .uriMapper(req -> req.url().encodedPath().replaceFirst("/bot[^/]*", ""))
                    .build();
        }
    }

}