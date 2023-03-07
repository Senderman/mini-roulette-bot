package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.service.UserService;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

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
    public void accept(@NotNull MessageContext ctx) {
        var user = userService.findById(ctx.message().getFrom().getId());
        if (user.getCoins() >= 300) {
            ctx.replyToMessage("Монетки можно запрашивать только если у вас меньше 300 монеток!").callAsync(ctx.sender);
            return;
        }
        if (ctx.message().getDate() - user.getLastCoinRequestDate() < TimeUnit.DAYS.toSeconds(1)) {
            ctx.replyToMessage("Монетки можно запрашивать только раз в день!").callAsync(ctx.sender);
            return;
        }
        user.setCoins(user.getCoins() + 300);
        user.setLastCoinRequestDate(ctx.message().getDate());
        userService.save(user);
        ctx.replyToMessage("✅ На ваш баланс успешно начислено 300 монеток!").callAsync(ctx.sender);
    }
}
