package play.station.bot.states;

import play.station.bot.util.TelegramBoot;

public class ChoseState extends State {
    public ChoseState(TelegramBoot telegramBoot) {
        super(telegramBoot);
    }

    @Override
    public void cancel(Long chatId) {
        telegramBoot.exit(chatId);
        telegramBoot.setState(chatId, new SearchState(telegramBoot));
    }

    @Override
    public void go(Long chatId, String text) {

    }
}
