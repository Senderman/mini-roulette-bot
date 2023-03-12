package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.service.UserService;
import io.micrometer.core.annotation.Counted;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class BalanceCommand implements CommandExecutor {

    private final UserService userService;

    public BalanceCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String command() {
        return "/balance";
    }

    @Override
    @Counted(value = "bot_command", extraTags = {"command", "/balance"})
    public void accept(@NotNull MessageContext ctx) {
        var user = userService.findById(ctx.message().getFrom().getId(), ctx.message().getFrom().getFirstName());
        String text = "\uD83D\uDCB0 Ваш баланс: %s".formatted(user.getCoins());
        if (user.getPendingCoins() != 0)
            text += " (%d сейчас в игре)".formatted(user.getPendingCoins());
        ctx.replyToMessage(text).callAsync(ctx.sender);
    }
}
