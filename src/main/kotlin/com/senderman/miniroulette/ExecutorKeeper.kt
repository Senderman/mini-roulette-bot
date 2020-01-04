package com.senderman.miniroulette

import com.senderman.miniroulette.commands.*
import com.senderman.neblib.AbstractExecutorKeeper

class ExecutorKeeper(handler: RouletteBotHandler): AbstractExecutorKeeper() {
    init {
        register(StartGame(handler))
        register(MakeBet(handler))
        register(Help(handler))
        register(GetCoins(handler))
        register(Balance(handler))
    }
}