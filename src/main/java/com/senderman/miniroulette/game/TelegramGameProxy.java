package com.senderman.miniroulette.game;

import com.annimon.tgbotsmodule.api.methods.Methods;
import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.game.bet.Bet;
import com.senderman.miniroulette.model.User;
import com.senderman.miniroulette.service.UserService;
import com.senderman.miniroulette.util.Html;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class TelegramGameProxy extends Game<Long> {

    private final MessageContext ctx;
    private final UserService userService;
    private final GameManager<Long, TelegramGameProxy> gameManager;
    private final List<Integer> messagesToDelete;

    public TelegramGameProxy(Long id, MessageContext ctx, UserService userService, GameManager<Long, TelegramGameProxy> gameManager) {
        super(id);
        this.ctx = ctx;
        this.userService = userService;
        this.gameManager = gameManager;
        this.messagesToDelete = new LinkedList<>();
    }

    public void addMessageToDelete(int messageId) {
        messagesToDelete.add(messageId);
    }

    @Override
    protected synchronized void spin() {
        addMessageToDelete(ctx.reply("❇️ Ставки кончились, ставок больше нет").call(ctx.sender).getMessageId());
        super.spin();

        int cell = Objects.requireNonNull(getCurrentCell());
        var players = getPlayers();
        var text = new StringBuilder("\uD83C\uDFB2 Итоги:\n");
        text.append(formatCell(cell)).append("\n\n");

        var usersToSave = new ArrayList<User>();
        for (var p : players) {
            var user = userService.findById(p.getId());
            user.setName(p.getName());
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
            text.append("\n\n");
            user.setPendingCoins(user.getPendingCoins() - pendingCoins);
            user.setCoins(user.getCoins() + p.getIncome());
            usersToSave.add(user);
        }
        userService.saveAll(usersToSave);
        ctx.reply(text.toString()).callAsync(ctx.sender);
        gameManager.delete(getId());
        messagesToDelete.forEach(mid -> Methods.deleteMessage(ctx.chatId(), mid).callAsync(ctx.sender));
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
        String sign = "";
        if (p.getDelta() == 0)
            emoji = "0️⃣";
        else if (p.getDelta() > 0) {
            emoji = "\uD83D\uDCC8";
            sign = "+";
        } else
            emoji = "\uD83D\uDCC9";
        return "%s %s%d".formatted(emoji, sign, p.getDelta());
    }
}
