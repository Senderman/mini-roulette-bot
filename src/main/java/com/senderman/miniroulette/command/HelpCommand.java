package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.confg.BotConfig;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@Singleton
public class HelpCommand implements CommandExecutor {

    private final BotConfig config;

    public HelpCommand(BotConfig config) {
        this.config = config;
    }

    @Override
    public String command() {
        return "/help";
    }

    @Override
    public Set<String> aliases() {
        return Set.of("/start");
    }

    @Override
    public void accept(@NotNull MessageContext ctx) {
        ctx.replyToMessage(config.getHelp()).callAsync(ctx.sender);
    }
}
