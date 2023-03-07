package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.model.User;
import com.senderman.miniroulette.service.UserService;
import com.senderman.miniroulette.util.Html;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RTopCommand implements CommandExecutor {

    private final UserService userService;

    public RTopCommand(UserService userService) {
        this.userService = userService;
    }

    @Override
    public String command() {
        return "/rtop";
    }

    @Override
    public void accept(@NotNull MessageContext ctx) {
        int counter = 1;
        var text = new StringBuilder("\uD83D\uDE0E <b>Топ богачей</b>\n\n");
        for (var user : userService.findTop10OrderByCoinsDesc()) {
            text.append(counter).append(". ").append(formatUser(user)).append("\n");
            counter++;
        }
        ctx.reply(text.toString()).callAsync(ctx.sender);
    }

    private String formatUser(User user) {
        return "%s (%d)".formatted(Html.htmlSafe(user.getName()), user.getCoins());
    }
}
