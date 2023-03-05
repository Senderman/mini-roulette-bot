package com.senderman.miniroulette.model.bet;

import com.senderman.miniroulette.model.Player;

sealed abstract class RangeBet extends Bet {

    private final int first;
    private final int last;

    RangeBet(int coefficient, int amount, Player player, int first, int last) {
        super(coefficient, amount, player, "%d-%s".formatted(first, last));
        this.first = first;
        this.last = last;
    }

    @Override
    public boolean isWin(int cell) {
        return cell >= first && cell <= last;
    }

    static final class Split extends RangeBet {

        public Split(int amount, Player player, int first, int last) {
            super(5, amount, player, first, last);
        }
    }

    static final class Trio extends RangeBet {

        public Trio(int amount, Player player, int first, int last) {
            super(3, amount, player, first, last);
        }
    }

    static final class Corner extends RangeBet {

        public Corner(int amount, Player player, int first, int last) {
            super(2, amount, player, first, last);
        }
    }
}
