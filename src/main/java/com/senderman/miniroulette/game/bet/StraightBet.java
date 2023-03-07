package com.senderman.miniroulette.game.bet;

final class StraightBet extends Bet {

    private final int target;

    StraightBet(int target, int amount) {
        super(11, amount, String.valueOf(target));
        this.target = target;
    }

    @Override
    public boolean isWin(int cell) {
        return cell == target;
    }

}
