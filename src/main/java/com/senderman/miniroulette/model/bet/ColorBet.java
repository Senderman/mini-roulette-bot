package com.senderman.miniroulette.model.bet;

import com.senderman.miniroulette.model.Player;

final class ColorBet extends Bet {

    private final Color color;

    ColorBet(int amount, Player player, Color color) {
        super(1, amount, player, color.target);
        this.color = color;
    }

    @Override
    public boolean isWin(int cell) {
        if (cell == 0)
            return false;

        if (cell % 2 == 0)
            return color == Color.BLACK;
        else
            return color == Color.RED;
    }

    enum Color {

        BLACK("черное"), RED("красное");

        private final String target;

        Color(String target) {
            this.target = target;
        }
    }
}
