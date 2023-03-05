package com.senderman.miniroulette;

import com.annimon.tgbotsmodule.BotHandler;
import com.annimon.tgbotsmodule.BotModule;
import com.annimon.tgbotsmodule.Runner;
import com.annimon.tgbotsmodule.beans.Config;
import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.ApplicationConfiguration;
import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.runtime.Micronaut;
import io.micronaut.scheduling.annotation.Scheduled;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MiniRouletteBot implements EmbeddedApplication<MiniRouletteBot>, BotModule {

    private final ApplicationContext context;
    private final ApplicationConfiguration configuration;
    private final BotHandler botHandler;

    public MiniRouletteBot(ApplicationContext context, ApplicationConfiguration configuration, BotHandler botHandler) {
        this.context = context;
        this.configuration = configuration;
        this.botHandler = botHandler;
    }

    public static void main(String[] args) {
        Micronaut.build(args)
                .classes(MiniRouletteBot.class)
                .environmentVariableIncludes(
                        "MONGODB_URI",
                        "BOT_USERNAME",
                        "BOT_TOKEN",
                        "MICRONAUT_APPLICATION_SERVER_HOST",
                        "MICRONAUT_APPLICATION_SERVER_PORT",
                        "MICRONAUT_METRICS_ENABLED"
                )
                .start();
    }

    @Scheduled(initialDelay = "1s")
    protected void runBot() {
        new Thread(() -> Runner.run(List.of(this))).start();
    }

    @Override
    public @NotNull BotHandler botHandler(@NotNull Config config) {
        return botHandler;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public ApplicationConfiguration getApplicationConfiguration() {
        return configuration;
    }

    @Override
    public boolean isServer() {
        return true;
    }

    @Override
    public boolean isRunning() {
        return true;
    }
}
