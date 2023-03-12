package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.game.GameManager;
import com.senderman.miniroulette.game.TelegramGameProxy;
import com.senderman.miniroulette.service.UserService;
import io.micrometer.core.annotation.Counted;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton
public class RunRouletteCommand implements CommandExecutor {

    private final static String gameField = """
            🎰 Делайте ваши ставки
                        
            0💚\s
            1❤️ 2🖤 3❤️ 4🖤 5❤️ 6🖤
            7❤️ 8🖤 9❤️ 10🖤 11❤️ 12🖤""";

    private final GameManager<Long, TelegramGameProxy> gameManager;
    private final UserService userService;

    public RunRouletteCommand(GameManager<Long, TelegramGameProxy> gameManager, UserService userService) {
        this.gameManager = gameManager;
        this.userService = userService;
    }

    @Override
    public String command() {
        return "/rourun";
    }

    @Override
    @Counted(value = "bot_command", extraTags = {"command", "/rourun"})
    public void accept(@NotNull MessageContext ctx) {
        var chatId = ctx.chatId();
        if (gameManager.exists(chatId)) {
            ctx.replyToMessage("Рулетка в этом чате уже запущена!").callAsync(ctx.sender);
            return;
        }
        var game = new TelegramGameProxy(chatId, ctx, userService, gameManager);
        gameManager.save(game);
        ctx.reply(gameField).callAsync(ctx.sender);
    }


}
