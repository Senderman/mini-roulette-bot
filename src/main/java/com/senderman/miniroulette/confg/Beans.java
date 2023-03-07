package com.senderman.miniroulette.confg;

import com.annimon.tgbotsmodule.analytics.UpdateHandler;
import com.annimon.tgbotsmodule.commands.CommandRegistry;
import com.annimon.tgbotsmodule.commands.RegexCommand;
import com.annimon.tgbotsmodule.commands.TextCommand;
import com.annimon.tgbotsmodule.commands.authority.Authority;
import com.annimon.tgbotsmodule.commands.authority.For;
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import org.telegram.telegrambots.bots.DefaultBotOptions;

import java.util.List;

@Factory
public class Beans {

    @Singleton
    public DefaultBotOptions botOptions() {
        var options = new DefaultBotOptions();
        options.setAllowedUpdates(List.of("message"));
        return options;
    }

    @Singleton
    public Authority<For> authority(BotConfig config) {
        return new SimpleAuthority(config.getCreatorId());
    }

    @Singleton
    public UpdateHandler commandRegistry(
            BotConfig config,
            Authority<For> authority,
            List<TextCommand> textCommands,
            List<RegexCommand> regexCommands
    ) {
        var registry = new CommandRegistry<>(config.getUsername(), authority);
        textCommands.forEach(registry::register);
        regexCommands.forEach(registry::register);
        return registry;
    }

}
