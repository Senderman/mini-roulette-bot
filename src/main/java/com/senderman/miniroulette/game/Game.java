package com.senderman.miniroulette.game;

import com.senderman.miniroulette.exception.TooLateException;
import com.senderman.miniroulette.exception.TooLittleCoinsException;
import com.senderman.miniroulette.game.bet.Bet;
import io.micronaut.core.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Game<ID> {

    public final static int MAX_WAIT_TIME_SECONDS = 30;

    private final ID id;
    private final Map<Long, Player> players; // players by id;
    private final AtomicInteger timer;
    private boolean isOpenForBets;
    @Nullable
    private Integer currentCell;

    public Game(ID id) {
        this.id = id;
        this.players = new HashMap<>();
        this.timer = new AtomicInteger(0);
        isOpenForBets = true;
        currentCell = null;
        runTimer();
    }

    public synchronized void addBet(Player player, Bet bet) throws TooLateException, TooLittleCoinsException {
        if (!isOpenForBets)
            throw new TooLateException();

        if (bet.getAmount() < 2)
            throw new TooLittleCoinsException();

        resetTimer();

        if (players.containsKey(player.getId()))
            players.get(player.getId()).addBet(bet);
        else {
            player.addBet(bet);
            players.put(player.getId(), player);
        }
    }

    private void runTimer() {
        ForkJoinPool.commonPool().execute(() -> {
            while (timer.get() < MAX_WAIT_TIME_SECONDS) {
                timer.incrementAndGet();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            spin();
        });
    }

    protected synchronized void spin() {
        isOpenForBets = false;
        currentCell = ThreadLocalRandom.current().nextInt(0, 13);

        if (currentCell == 0)
            processZero();
        else
            processNonZero(currentCell);
    }

    protected void processNonZero(int cell) {
        for (var player : getPlayers()) {
            int delta = 0;
            for (var bet : player.getBets()) {
                if (bet.isWin(cell)) {
                    delta += bet.getPay();
                } else {
                    delta -= bet.getAmount();
                }
            }
            player.setDelta(delta);
        }
    }

    protected void processZero() {
        for (var player : getPlayers()) {
            int delta = 0;
            for (var bet : player.getBets()) {
                if (bet.isWin(0)) {
                    delta += bet.getPay();
                } else {
                    delta -= bet.getAmount() / 2;
                }
            }
            player.setDelta(delta);
        }
    }

    private void resetTimer() {
        timer.set(0);
    }

    public ID getId() {
        return id;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    @Nullable
    public synchronized Integer getCurrentCell() {
        return currentCell;
    }
}
