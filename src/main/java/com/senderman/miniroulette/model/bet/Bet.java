package com.senderman.miniroulette.model.bet;

import com.annimon.tgbotsmodule.commands.context.MessageContext;
import com.senderman.miniroulette.exception.InvalidBetCommandException;
import com.senderman.miniroulette.exception.InvalidBetRangeException;
import com.senderman.miniroulette.model.Player;
import com.senderman.miniroulette.util.Html;

public sealed abstract class Bet permits StraightBet, ColorBet, RangeBet {

    private final int coefficient; // depends on the type of the bet
    private final int amount; // how much money at stake
    private final Player player; // owner of the bet
    private final String targetAsString; // string representation of the target cell, used in output

    public Bet(int coefficient, int amount, Player player, String targetAsString) {
        this.coefficient = coefficient;
        this.amount = amount;
        this.player = player;
        this.targetAsString = targetAsString;
    }

    public abstract boolean isWin(int cell);

    public int getPay() {
        return amount * coefficient;
    }

    public int getCoefficient() {
        return coefficient;
    }

    public int getAmount() {
        return amount;
    }

    public Player getPlayer() {
        return player;
    }

    public String getTargetAsString() {
        return targetAsString;
    }

    private Bet parseBet(MessageContext ctx) throws InvalidBetRangeException, InvalidBetCommandException {
        final String[] params = ctx.message().getText().split("\\s+");
        if (params.length != 2)
            throw new InvalidBetCommandException();

        final int amount;
        try {
            amount = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new InvalidBetCommandException();
        }

        final var player = new Player(ctx.user().getId(), Html.htmlSafe(ctx.user().getFirstName()));
        final var target = params[1];

        if (target.matches("ч([её]рное)?|к(расное)?")) {
            ColorBet.Color color = (target.charAt(0) == 'ч') ? ColorBet.Color.BLACK : ColorBet.Color.RED;
            return new ColorBet(amount, player, color);
        }

        if (target.matches("\\d+")) {
            int cell = Integer.parseInt(target);
            if (cell < 0 || cell > 12)
                throw new InvalidBetRangeException();
            return new StraightBet(cell, amount, player);
        }

        if (target.matches("\\d+-\\d+")) {
            String[] range = target.split("-");
            int first = Integer.parseInt(range[0]);
            int last = Integer.parseInt(range[1]);
            if (first < 0 || last > 12 || first >= last)
                throw new InvalidBetRangeException();

            return switch (last - first) {
                case 1 -> new RangeBet.Split(amount, player, first, last);
                case 2 -> new RangeBet.Trio(amount, player, first, last);
                case 3 -> new RangeBet.Corner(amount, player, first, last);
                default -> throw new InvalidBetRangeException();
            };
        }
        throw new InvalidBetCommandException();
    }
}
