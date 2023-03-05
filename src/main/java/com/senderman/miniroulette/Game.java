package com.senderman.miniroulette;

import com.senderman.miniroulette.exception.NotEnoughCoinsException;
import com.senderman.miniroulette.exception.TooLateException;
import com.senderman.miniroulette.model.Player;
import com.senderman.miniroulette.model.bet.Bet;
import io.micronaut.core.annotation.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Game {

    public final static int MAX_WAIT_TIME_SECONDS = 30;

    private final long id;
    @Nullable
    private final Consumer<Game> onSpin;
    @Nullable
    private final Consumer<Game> onGameEnd;
    private final Map<Long, Player> players; // players by id;
    private final AtomicInteger timer;
    private boolean isOpenForBets;
    @Nullable
    private Integer currentCell;

    public Game(long id, @Nullable Consumer<Game> onSpin, @Nullable Consumer<Game> onGameEnd) {
        this.id = id;
        this.onSpin = onSpin;
        this.onGameEnd = onGameEnd;
        this.players = new HashMap<>();
        this.timer = new AtomicInteger(0);
        isOpenForBets = true;
        currentCell = null;
    }

    public synchronized void addBet(Player player, Bet bet) throws TooLateException, NotEnoughCoinsException {
        if (!isOpenForBets)
            throw new TooLateException();

        if (bet.getAmount() < 2)
            throw new NotEnoughCoinsException();

        if (players.containsKey(player.getId()))
            players.get(player.getId()).addBet(bet);
        else {
            player.addBet(bet);
            players.put(player.getId(), player);
        }
    }

    private void runTimer() {
        new Thread(() -> {
            while (timer.get() < MAX_WAIT_TIME_SECONDS) {
                timer.incrementAndGet();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            spin();
        }).start();
    }

    private synchronized void spin() {
        if (onSpin != null)
            onSpin.accept(this);

        isOpenForBets = false;
        currentCell = ThreadLocalRandom.current().nextInt(0, 13);

        if (currentCell == 0)
            processZero();
        else
            processNonZero(currentCell);

        if (onGameEnd != null)
            onGameEnd.accept(this);
    }

    private void processNonZero(int cell) {
        for (var player : getPlayers()) {
            int income = 0;
            int delta = 0;
            for (var bet : player.getBets()) {
                if (bet.isWin(cell)) {
                    income += bet.getPay() + bet.getAmount();
                    delta += bet.getPay();
                } else {
                    delta -= bet.getAmount();
                }
            }
            player.setIncome(income);
            player.setDelta(delta);
        }
    }

    private void processZero() {
        for (var player : getPlayers()) {
            int income = 0;
            int delta = 0;
            for (var bet : player.getBets()) {
                if (bet.isWin(0)) {
                    income += bet.getPay() + bet.getAmount();
                    delta += bet.getPay();
                } else {
                    income += bet.getAmount() / 2;
                    delta -= bet.getAmount() / 2;
                }
            }
            player.setIncome(income);
            player.setDelta(delta);
        }
    }

    public long getId() {
        return id;
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }

    public AtomicInteger getTimer() {
        return timer;
    }

    @Nullable
    public synchronized Integer getCurrentCell() {
        return currentCell;
    }
}
