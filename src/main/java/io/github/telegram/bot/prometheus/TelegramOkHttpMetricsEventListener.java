package io.github.telegram.bot.prometheus;

import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import okhttp3.EventListener;

/**
 * Factory for creating {@link EventListener} instances that collect metrics
 * for Telegram Bot API HTTP requests using Micrometer and Prometheus.
 *
 * <p>This class provides a convenient way to instrument OkHttp client calls
 * to the Telegram Bot API with metrics collection. It automatically sanitizes
 * the request URIs by removing the bot token from the path to prevent
 * sensitive data from appearing in metrics.</p>
 *
 * Example usage:
 * <pre>{@code
 * PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
 * OkHttpClient client = new OkHttpClient.Builder()
 *     .eventListener(TelegramOkHttpMetricsEventListener.builder()
 *         .registry(registry)
 *         .name("telegram.api.requests")
 *         .build())
 *     .build();
 * }</pre>

 * <p>URIs are sanitized by removing the bot token from paths. For example:</p>
 * <ul>
 *   <li>{@code /bot123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11/sendMessage} → {@code /sendMessage}</li>
 *   <li>{@code /botTOKEN/getUpdates} → {@code /getUpdates}</li>
 * </ul>
 *
 * @see io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
 * @see okhttp3.EventListener
 */
public final class TelegramOkHttpMetricsEventListener {

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class that should only be used through its builder.
     */
    private TelegramOkHttpMetricsEventListener() {
        throw new IllegalStateException("This is a builder class");
    }

    /**
     * Creates a new builder for configuring a Telegram-specific HTTP metrics event listener.
     *
     * @return a new {@link Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for configuring {@link EventListener} instances that collect
     * metrics for Telegram Bot API HTTP requests.
     *
     * <p>Use this builder to configure the metrics registry and metric name
     * before building the event listener.</p>
     */
    public static class Builder {

        private PrometheusMeterRegistry registry;
        private String name = "http.request";

        /**
         * Sets the Prometheus meter registry where metrics will be published.
         * This parameter is required.
         *
         * @param registry the Prometheus meter registry to use
         * @return this builder instance for method chaining
         */
        public Builder registry(final PrometheusMeterRegistry registry) {
            this.registry = registry;
            return this;
        }

        /**
         * Sets the base name for the metrics. Defaults to "http.request".
         *
         * @param name the base name for the metrics
         * @return this builder instance for method chaining
         */
        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * Builds the configured {@link EventListener} instance.
         *
         * @return a new EventListener configured for Telegram Bot API metrics collection
         * @throws IllegalStateException if registry has not been set
         */
        public EventListener build() {
            if (registry == null) {
                throw new IllegalStateException("Registry must be set");
            }

            OkHttpMetricsEventListener.Builder builder = OkHttpMetricsEventListener
                    .builder(registry, name);

            return builder
                    .uriMapper(req -> req.url().encodedPath().replaceFirst("/bot[^/]*", ""))
                    .build();
        }
    }
}