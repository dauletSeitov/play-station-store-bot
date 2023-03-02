package play.station.bot.states;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.util.TelegramBoot;
@RequiredArgsConstructor
public abstract class State {
    protected final TelegramBoot telegramBoot;

    public abstract void cancel(Long chatId);

    public abstract void go(Update update);
}
