# Telegram bot prometheus integration

A library for Java telegram bot that provides prometheus metrics integration with https://rubenlagus.github.io/TelegramBotsDocumentation/telegram-bots.html.

## 📦 Installation

Add the library to your project dependencies:

### Maven

```xml
<dependency>
    <groupId>io.github.sbushmelev</groupId>
    <artifactId>telegram-bot-prometheus</artifactId>
    <version>1.0.0</version>
</dependency>
```
### Gradle
```
implementation("io.github:telegram-bot-prometheus:1.0.0")
```

## 🚀 Quick Start

```java
//For TelegramClient
public class MyBot {
    public static void main(String[] args) {
         PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
         OkHttpClient client = new OkHttpClient.Builder()
                     .eventListener(TelegramOkHttpMetricsEventListener.builder()
                         .registry(registry)
                         .name("telegram.api.requests")
                         .build())
                  .build();
        TelegramClient telegramClient = new OkHttpTelegramClient(client, TERLEGRAM_TOKEN); 
    }
}
```

```java
//For longPollingInstance
public class MyBot {
    public static void main(String[] args) {
         PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
         OkHttpClient client = new OkHttpClient.Builder()
                     .eventListener(TelegramOkHttpMetricsEventListener.builder()
                         .registry(registry)
                         .name("telegram.api.requests")
                         .build())
                  .build();
         
        TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication(
                ObjectMapper::new,
                () -> client);
        botsApplication.registerBot(token, new LongPollingBotInstance());
    }
}
```

## 📜 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

### Third-Party Dependencies

This library depends on the following open-source components:

*    **Core Dependencies:**

     * OkHttp 4.12.0 - Square, Inc. (Apache License 2.0)
     * Micrometer Core 1.11.5 - VMware, Inc. (Apache License 2.0)
     * Micrometer Prometheus 1.11.5 - VMware, Inc. (Apache License 2.0)

*    **Test Dependencies:**

     * JUnit Jupiter 5.10.0 - JUnit Team (Eclipse Public License 2.0)
     * AssertJ 3.27.6 - AssertJ (Apache License 2.0)
     * OkHttp MockWebServer 4.12.0 - Square, Inc. (Apache License 2.0)

A complete notice of third-party components and their licenses can be found in the [NOTICE](NOTICE) file.