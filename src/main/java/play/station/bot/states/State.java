package play.station.bot.states;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.util.TelegramBoot;
import play.station.bot.util.Utils;

@RequiredArgsConstructor
public abstract class State {
    protected final TelegramBoot telegramBoot;

    public void cancel(Long chatId) {
        telegramBoot.cancel(chatId);
        telegramBoot.setState(chatId, new StartState(telegramBoot));
    }

    public void go(Update update) {
        telegramBoot.cancel(Utils.getChatId(update));
    }

    public void unsubscribe(Update update) {
//        telegramBoot.exit(update.getMessage().getChatId());
    }

    public void search(Update update) {
//        telegramBoot.exit(update.getMessage().getChatId());
    }

    public void subscriptions(Update update) {
//        telegramBoot.exit(update.getMessage().getChatId());
    }

}
