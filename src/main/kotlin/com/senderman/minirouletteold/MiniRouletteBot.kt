package com.senderman.miniroulette

import com.annimon.tgbotsmodule.BotHandler
import com.annimon.tgbotsmodule.BotModule
import com.annimon.tgbotsmodule.Runner
import com.annimon.tgbotsmodule.beans.Config
import com.annimon.tgbotsmodule.services.YamlConfigLoaderService
import java.io.File
import java.io.FileOutputStream

class MiniRouletteBot : BotModule {

    private val configDir = File("botConfigs")
    private val configFile = File("${configDir.name}/roulette.yaml")

    private fun unpackConfig() {
        if (!configDir.exists())
            configDir.mkdir()
        if (!configDir.isDirectory)
            throw Exception(
                "${configDir.name} is not a directory! Please, remove the \"${configDir.name}\" file from project's root!"
            )

        if (configFile.exists())
            return
        val configFileRes = this::class.java.getResourceAsStream("/roulette.yaml")
        val buffer = ByteArray(configFileRes.available())
        configFileRes.read(buffer)
        val outStream = FileOutputStream(configFile)
        outStream.write(buffer)
        outStream.flush()
        outStream.close()
    }

    override fun botHandler(config: Config): BotHandler {
        unpackConfig()
        val configLoader = YamlConfigLoaderService<BotConfig>()
        val botConfig = configLoader.load(configFile, BotConfig::class.java)
        Services.botConfig = botConfig
        return RouletteBotHandler()
    }
}

