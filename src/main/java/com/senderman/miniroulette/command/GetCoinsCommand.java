package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.service.UserService;
import io.micrometer.core.annotation.Counted;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;

@Singleton
public class GetCoinsCommand implements CommandExecutor {

    private final UserService userService;

    public GetCoinsCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String command() {
        return "/getcoins";
    }

    @Override
    @Counted(value = "bot_command", extraTags = {"command", "/getcoins"})
    public void accept(@NotNull MessageContext ctx) {
        var user = userService.findById(ctx.message().getFrom().getId(), ctx.message().getFrom().getFirstName());
        if (user.getCoins() >= 300) {
            ctx.replyToMessage("Монетки можно запрашивать только если у вас меньше 300 монеток!").callAsync(ctx.sender);
            return;
        }
        var currentTime = LocalDateTime.now();
        if (Duration.between(user.getLastCoinRequestDate().toLocalDateTime(), currentTime).toHours() < 24) {
            ctx.replyToMessage("Монетки можно запрашивать только раз в день!").callAsync(ctx.sender);
            return;
        }

        userService.increaseCoinsSetLastCoinRequestDate(user.getUserId(), 300, Timestamp.valueOf(currentTime));
        ctx.replyToMessage("✅ На ваш баланс успешно начислено 300 монеток!").callAsync(ctx.sender);
    }
}
