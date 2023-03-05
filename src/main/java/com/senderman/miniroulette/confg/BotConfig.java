package com.senderman.miniroulette.confg;

import io.micronaut.context.annotation.ConfigurationProperties;

@ConfigurationProperties("bot")
public interface BotConfig {

    String getUsername();

    String getToken();

    String getHelp();

}
