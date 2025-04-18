package com.senderman.miniroulette.config;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("bot")
public record BotConfig(
        String username,
        String token,
        String help,
        long creatorId
) {

}
