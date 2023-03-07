package com.senderman.miniroulette.command;

import com.annimon.tgbotsmodule.commands.RegexCommand;
import com.annimon.tgbotsmodule.commands.authority.For;
import com.annimon.tgbotsmodule.commands.context.RegexMessageContext;
import com.senderman.miniroulette.exception.InvalidBetCommandException;
import com.senderman.miniroulette.exception.InvalidBetRangeException;
import com.senderman.miniroulette.exception.TooLateException;
import com.senderman.miniroulette.exception.TooLittleCoinsException;
import com.senderman.miniroulette.game.GameManager;
import com.senderman.miniroulette.game.Player;
import com.senderman.miniroulette.game.bet.Bet;
import com.senderman.miniroulette.service.UserService;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.regex.Pattern;

@Singleton
public class BetRegexCommand implements RegexCommand {

    private final Pattern pattern = Pattern.compile("\\d+\\s+(?:ч(?:[её]рное)?|к(?:расное)?|\\d{1,2}(:?-\\d{1,2})?)");
    private final GameManager<Long> gameManager;
    private final UserService userService;

    public BetRegexCommand(GameManager<Long> gameManager, UserService userService) {
        this.gameManager = gameManager;
        this.userService = userService;

    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public EnumSet<For> authority() {
        return For.all();
    }

    @Override
    public void accept(@NotNull RegexMessageContext ctx) {
        var game = gameManager.get(ctx.chatId());
        if (game == null)
            return;

        final Bet bet;
        try {
            bet = Bet.parseBet(ctx.message().getText());
        } catch (InvalidBetRangeException e) {
            ctx.replyToMessage("Неверный дипазон ставки!").callAsync(ctx.sender);
            return;
        } catch (InvalidBetCommandException ignored) {
            return;
        }
        var from = ctx.message().getFrom();
        var player = new Player(from.getId(), from.getFirstName());
        try {
            var user = userService.findById(player.getId());
            user.setPendingCoins(user.getPendingCoins() + bet.getAmount());
            user.setCoins(user.getCoins() - bet.getAmount());
            userService.save(user);
            game.addBet(player, bet);
            ctx.replyToMessage("Ставка принята!").callAsync(ctx.sender);
        } catch (TooLateException e) {
            ctx.replyToMessage("Слишком поздно! Ставки больше не принимаются!").callAsync(ctx.sender);
        } catch (TooLittleCoinsException e) {
            ctx.replyToMessage("Минимальная ставка - 2!").callAsync(ctx.sender);
        }
    }
}
