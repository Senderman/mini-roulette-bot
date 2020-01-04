package com.senderman.miniroulette

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService

class MiniRouletteBot : BotModule {
    override fun botHandler(config: Config): BotHandler {
        val configLoader = YamlConfigLoaderService<BotConfig>()
        val configFile = configLoader.configFile("/botConfigs/roulette", config.profile)
        val botConfig = configLoader.load(configFile, BotConfig::class.java)
        Services.botConfig = botConfig
        return RouletteBotHandler()
    }
}

fun main(args: Array<String>) {
    val profile = if (args.isNotEmpty() && args[0].isNotBlank()) args[0] else ""
    Runner.run(profile, listOf(MiniRouletteBot()))
}

