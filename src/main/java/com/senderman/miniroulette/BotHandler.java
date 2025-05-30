package com.senderman.miniroulette;

import com.annimon.tgbotsmodule.BotModuleOptions;
import com.annimon.tgbotsmodule.analytics.UpdateHandler;
import com.senderman.miniroulette.config.BotConfig;
import com.senderman.miniroulette.service.UserService;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Set;

@Singleton
public class BotHandler extends com.annimon.tgbotsmodule.BotHandler {

    private final BotConfig config;
    private final UpdateHandler updateHandler;
    private final Set<Long> telegramServiceUserIds;

    public BotHandler(@NotNull BotModuleOptions options, BotConfig config, UpdateHandler updateHandler, UserService userService) {
        super(options);
        this.config = config;
        this.updateHandler = updateHandler;
        this.telegramServiceUserIds = Set.of(
                777000L, // attached channel's messages
                1087968824L, // anonymous group admin @GroupAnonymousBot
                136817688L // Channel message, @Channel_Bot
        );

        addMethodPreprocessor(SendMessage.class, m -> m.enableHtml(true));
        userService.restoreLostCoins();
    }

    @Override
    protected BotApiMethod<?> onUpdate(@NotNull Update update) {
        final var message = update.getMessage();

        // do not process messages older than 2 minutes
        if (message.getDate() + 120 < System.currentTimeMillis() / 1000)
            return null;

        if (telegramServiceUserIds.contains(message.getFrom().getId())) {
            return null;
        }

        updateHandler.handleUpdate(this, update);

        return null;
    }

    @Override
    public void handleTelegramApiException(TelegramApiException ex) {
        // enable only if really needed
    }
}
