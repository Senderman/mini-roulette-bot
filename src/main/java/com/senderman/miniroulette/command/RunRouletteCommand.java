package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.game.Game;
import com.senderman.miniroulette.game.GameManager;
import com.senderman.miniroulette.game.Player;
import com.senderman.miniroulette.game.bet.Bet;
import com.senderman.miniroulette.model.User;
import com.senderman.miniroulette.service.UserService;
import com.senderman.miniroulette.util.Html;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

@Singleton
public class RunRouletteCommand implements CommandExecutor {

    private final static String gameField = """
            🎰 Делайте ваши ставки
                        
            0💚\s
            1❤️ 2🖤 3❤️ 4🖤 5❤️ 6🖤
            7❤️ 8🖤 9❤️ 10🖤 11❤️ 12🖤""";

    private final GameManager<Long> gameManager;
    private final UserService userService;

    public RunRouletteCommand(GameManager<Long> gameManager, UserService userService) {
        this.gameManager = gameManager;
        this.userService = userService;
    }

    @Override
    public String command() {
        return "/rourun";
    }

    @Override
    public void accept(@NotNull MessageContext ctx) {
        var chatId = ctx.chatId();
        if (gameManager.exists(chatId)) {
            ctx.replyToMessage("Рулетка в этом чате уже запущена!").callAsync(ctx.sender);
            return;
        }
        var game = new Game<>(chatId, g -> onSpin(g, ctx), g -> onGameEnd(g, ctx));
        gameManager.save(game);
        ctx.reply(gameField).callAsync(ctx.sender);
    }

    private void onSpin(Game<Long> game, MessageContext ctx) {
        ctx.reply("❇️ Ставки кончились, ставок больше нет").callAsync(ctx.sender);
    }

    private void onGameEnd(Game<Long> game, MessageContext ctx) {
        int cell = Objects.requireNonNull(game.getCurrentCell());
        var players = game.getPlayers();
        var text = new StringBuilder("\uD83C\uDFB2 Итоги:\n");
        text.append(formatCell(cell)).append("\n\n");

        var usersToSave = new ArrayList<User>();
        for (var p : players) {
            var user = userService.findById(p.getId());
            text.append("<b>").append(Html.htmlSafe(p.getName())).append("</b>:\n");
            p.getBets()
                    .stream()
                    .map(b -> formatBet(b, cell))
                    .forEach(b -> text.append(b).append("\n"));
            final int pendingCoins = p.getBets()
                    .stream()
                    .mapToInt(Bet::getAmount)
                    .sum();
            text.append(formatStonks(p));
            text.append("\n");
            user.setPendingCoins(user.getPendingCoins() - pendingCoins);
            user.setCoins(user.getCoins() + p.getIncome());
            usersToSave.add(user);
        }
        userService.saveAll(usersToSave);
        ctx.reply(text.toString()).callAsync(ctx.sender);
        gameManager.delete(game.getId());
    }

    private String formatCell(int value) {
        if (value == 0)
            return "\uD83D\uDC9A 0";

        return (value % 2 == 0 ? "\uD83D\uDDA4" : "❤️") + value;
    }

    private String formatBet(Bet b, int cell) {
        boolean isWin = b.isWin(cell);
        var emoji = isWin ? "\uD83D\uDE0E" : "\uD83D\uDE14";
        var sign = isWin ? "+" : "-";
        int cash = isWin ? b.getPay() : b.getAmount();
        return "%s %s%d (%d на %s)".formatted(emoji, sign, cash, b.getAmount(), b.getTargetAsString());
    }

    private String formatStonks(Player p) {
        final String emoji;
        if (p.getDelta() == 0)
            emoji = "0️⃣";
        else if (p.getDelta() > 0)
            emoji = "\uD83D\uDCC8";
        else
            emoji = "\uD83D\uDCC9";
        return "%s %d".formatted(emoji, p.getDelta());
    }
}
