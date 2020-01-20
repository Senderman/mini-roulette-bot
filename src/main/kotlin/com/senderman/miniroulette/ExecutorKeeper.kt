package com.senderman.miniroulette

import com.senderman.miniroulette.commands.*
import com.senderman.neblib.AbstractExecutorKeeper

class ExecutorKeeper(handler: RouletteBotHandler) : AbstractExecutorKeeper() {
    init {
        register(StartGame(handler))
        register(Help(handler, commandExecutors))
        register(GetCoins(handler))
        register(Balance(handler))
        register(Top(handler))
        register(GiveMoney(handler))
        register(BuyCoins(handler))
        register(Reward(handler))
    }
}