package com.senderman.miniroulette;

import com.annimon.tgbotsmodule.BotHandler;
import com.annimon.tgbotsmodule.BotModule;
import com.annimon.tgbotsmodule.Runner;
import com.annimon.tgbotsmodule.beans.Config;
import io.micronaut.context.event.StartupEvent;
import io.micronaut.runtime.Micronaut;
import io.micronaut.runtime.event.annotation.EventListener;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Singleton
public class MiniRouletteBot implements BotModule {

    private final BotHandler botHandler;

    public MiniRouletteBot(BotHandler botHandler) {
        this.botHandler = botHandler;
    }

    public static void main(String[] args) {
        Micronaut.build(args)
                .classes(MiniRouletteBot.class)
                .banner(!System.getProperties().containsKey("disableBanner"))
                .start();
    }

    @EventListener
    protected void run(StartupEvent event) {
        Runner.run(List.of(this));
    }

    @Override
    public @NotNull BotHandler botHandler(@NotNull Config config) {
        return botHandler;
    }


}
