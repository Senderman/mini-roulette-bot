package com.senderman.miniroulette.game.bet;

import com.senderman.miniroulette.exception.InvalidBetCommandException;
import com.senderman.miniroulette.exception.InvalidBetRangeException;

public sealed abstract class Bet permits StraightBet, ColorBet, RangeBet {

    private final int coefficient; // depends on the type of the bet
    private final int amount; // how much money at stake
    private final String targetAsString; // string representation of the target cell, used in output

    public Bet(int coefficient, int amount, String targetAsString) {
        this.coefficient = coefficient;
        this.amount = amount;
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

    public String getTargetAsString() {
        return targetAsString;
    }

    public static Bet parseBet(String text) throws InvalidBetRangeException, InvalidBetCommandException {
        final String[] params = text.split("\\s+");
        if (params.length != 2)
            throw new InvalidBetCommandException();

        final int amount;
        try {
            amount = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            throw new InvalidBetCommandException();
        }

        final var target = params[1];

        if (target.matches("ч([её]рное)?|к(расное)?")) {
            ColorBet.Color color = (target.charAt(0) == 'ч') ? ColorBet.Color.BLACK : ColorBet.Color.RED;
            return new ColorBet(amount, color);
        }

        if (target.matches("\\d+")) {
            int cell = Integer.parseInt(target);
            if (cell < 0 || cell > 12)
                throw new InvalidBetRangeException();
            return new StraightBet(cell, amount);
        }

        if (target.matches("\\d+-\\d+")) {
            String[] range = target.split("-");
            int first = Integer.parseInt(range[0]);
            int last = Integer.parseInt(range[1]);
            if (first < 0 || last > 12 || first >= last)
                throw new InvalidBetRangeException();

            return switch (last - first) {
                case 1 -> new RangeBet.Split(amount, first, last);
                case 2 -> new RangeBet.Trio(amount, first, last);
                case 3 -> new RangeBet.Corner(amount, first, last);
                default -> throw new InvalidBetRangeException();
            };
        }
        throw new InvalidBetCommandException();
    }
}
