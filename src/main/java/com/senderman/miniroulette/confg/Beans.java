package com.senderman.miniroulette.confg;

import com.annimon.tgbotsmodule.analytics.UpdateHandler;
import com.annimon.tgbotsmodule.commands.CommandRegistry;
import com.annimon.tgbotsmodule.commands.authority.Authority;
import com.annimon.tgbotsmodule.commands.authority.For;
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority;
import com.senderman.miniroulette.command.CommandExecutor;
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
    public UpdateHandler commandRegistry(BotConfig config, Authority<For> authority, List<CommandExecutor> executors) {
        var registry = new CommandRegistry<>(config.getUsername(), authority);
        executors.forEach(registry::register);
        return registry;
    }

}
