package com.senderman.miniroulette.model.bet;

sealed abstract class RangeBet extends Bet {

    private final int first;
    private final int last;

    RangeBet(int coefficient, int amount, int first, int last) {
        super(coefficient, amount, "%d-%s".formatted(first, last));
        this.first = first;
        this.last = last;
    }

    @Override
    public boolean isWin(int cell) {
        return cell >= first && cell <= last;
    }

    static final class Split extends RangeBet {

        public Split(int amount, int first, int last) {
            super(5, amount, first, last);
        }
    }

    static final class Trio extends RangeBet {

        public Trio(int amount, int first, int last) {
            super(3, amount, first, last);
        }
    }

    static final class Corner extends RangeBet {

        public Corner(int amount, int first, int last) {
            super(2, amount, first, last);
        }
    }
}
