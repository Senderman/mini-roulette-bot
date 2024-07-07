package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PrivacyPolicyCommand implements CommandExecutor{

    private final String privacyPolicyLink;

    public PrivacyPolicyCommand(@Value("${bot.privacyPolicyLink}") String privacyPolicyLink) {
        this.privacyPolicyLink = privacyPolicyLink;
    }

    @Override
    public String command() {
        return "/privacy";
    }

    @Override
    public void accept(@NotNull MessageContext ctx) {
        ctx.replyToMessage(privacyPolicyLink).callAsync(ctx.sender);
    }
}
