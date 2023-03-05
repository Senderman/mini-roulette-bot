package com.senderman.miniroulette;

import com.senderman.miniroulette.confg.BotConfig;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Singleton
public class BotHandler extends com.annimon.tgbotsmodule.BotHandler {

    private final BotConfig config;

    public BotHandler(@NotNull DefaultBotOptions options, BotConfig config) {
        super(options, config.getToken());
        this.config = config;
    }

    @Override
    protected BotApiMethod<?> onUpdate(@NotNull Update update) {
        return null;
    }

    @Override
    public String getBotUsername() {
        return config.getUsername();
    }
}
