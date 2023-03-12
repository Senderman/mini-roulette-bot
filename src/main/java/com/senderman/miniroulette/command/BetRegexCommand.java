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
import com.senderman.miniroulette.game.TelegramGameProxy;
import com.senderman.miniroulette.game.bet.Bet;
import com.senderman.miniroulette.service.UserService;
import io.micrometer.core.annotation.Counted;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.EnumSet;
import java.util.regex.Pattern;

@Singleton
public class BetRegexCommand implements RegexCommand {

    private final Pattern pattern = Pattern.compile("\\d+\\s+(?:ч(?:[её]рное)?|к(?:расное)?|\\d{1,2}(:?-\\d{1,2})?)");
    private final GameManager<Long, TelegramGameProxy> gameManager;
    private final UserService userService;

    public BetRegexCommand(GameManager<Long, TelegramGameProxy> gameManager, UserService userService) {
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
    @Counted(value = "bot_command", extraTags = {"command", "makebet"})
    public void accept(@NotNull RegexMessageContext ctx) {
        var game = gameManager.get(ctx.chatId());
        if (game == null)
            return;

        deleteLater(game, ctx.message());
        var from = ctx.message().getFrom();
        var user = userService.findById(from.getId(), from.getFirstName());
        final Bet bet;
        try {
            bet = Bet.parseBet(ctx.message().getText());
            if (bet.getAmount() > user.getCoins()) {
                deleteLater(game, ctx.replyToMessage("У вас недостаточно монет!").call(ctx.sender));
                return;
            }
        } catch (InvalidBetRangeException e) {
            deleteLater(game, ctx.replyToMessage("Неверный диапазон ставки!").call(ctx.sender));
            return;
        } catch (InvalidBetCommandException ignored) {
            return;
        }
        var player = new Player(from.getId(), from.getFirstName());
        try {
            game.addBet(player, bet);
            deleteLater(game, ctx.replyToMessage("Ставка принята!").call(ctx.sender));
            int amount = bet.getAmount();
            userService.updateCoins(user.getUserId(), from.getFirstName(), -amount, amount);
        } catch (TooLateException e) {
            deleteLater(game, ctx.replyToMessage("Слишком поздно! Ставки больше не принимаются!").call(ctx.sender));
        } catch (TooLittleCoinsException e) {
            deleteLater(game, ctx.replyToMessage("Минимальная ставка - 2!").call(ctx.sender));
        }
    }

    private void deleteLater(TelegramGameProxy game, Message message) {
        game.addMessageToDelete(message.getMessageId());
    }
}
