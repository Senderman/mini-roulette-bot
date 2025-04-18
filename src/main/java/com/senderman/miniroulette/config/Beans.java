package com.senderman.miniroulette.config;

import com.annimon.tgbotsmodule.BotModuleOptions;
import com.annimon.tgbotsmodule.analytics.UpdateHandler;
import com.annimon.tgbotsmodule.commands.CommandRegistry;
import com.annimon.tgbotsmodule.commands.RegexCommand;
import com.annimon.tgbotsmodule.commands.TextCommand;
import com.annimon.tgbotsmodule.commands.authority.Authority;
import com.annimon.tgbotsmodule.commands.authority.For;
import com.annimon.tgbotsmodule.commands.authority.SimpleAuthority;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;

import java.util.List;

@Factory
public class Beans {

    @Singleton
    public BotModuleOptions botOptions(BotConfig config) {
        return BotModuleOptions.create(config.token())
                .telegramUrlSupplierDefault()
                .getUpdatesGeneratorDefaultWithAllowedUpdates(List.of("message"))
                .build();
    }

    @Singleton
    public Authority<For> authority(BotConfig config) {
        return new SimpleAuthority(config.creatorId());
    }

    @Singleton
    public UpdateHandler commandRegistry(
            BotConfig config,
            Authority<For> authority,
            List<TextCommand> textCommands,
            List<RegexCommand> regexCommands
    ) {
        var registry = new CommandRegistry<>(config.username(), authority);
        textCommands.forEach(registry::register);
        regexCommands.forEach(registry::register);
        return registry;
    }

}
