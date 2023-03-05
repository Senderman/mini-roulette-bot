package com.senderman.miniroulette.model.bet;

import com.senderman.miniroulette.model.Player;

final class StraightBet extends Bet {

    private final int target;

    StraightBet(int target, int amount, Player player) {
        super(11, amount, player, String.valueOf(target));
        this.target = target;
    }

    @Override
    public boolean isWin(int cell) {
        return cell == target;
    }

}
