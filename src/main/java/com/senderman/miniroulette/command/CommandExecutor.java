package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.TextCommand;
import com.annimon.tgbotsmodule.commands.authority.For;

import java.util.EnumSet;

public interface CommandExecutor extends TextCommand {

    @Override
    default EnumSet<For> authority() {
        return For.all();
    }

}
