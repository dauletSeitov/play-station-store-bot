package play.station.bot.states;

import play.station.bot.model.entities.Product;
import play.station.bot.util.TelegramBoot;
import java.util.List;

public class SearchState extends State {
    public SearchState(TelegramBoot telegramBoot) {
        super(telegramBoot);
    }

    @Override
    public void cancel(Long chatId) {
        telegramBoot.exit(chatId);
    }

    @Override
    public void go(Long chatId, String text) {
        List<Product> list = telegramBoot.search(chatId, text);
        if (list.isEmpty()) return;
        telegramBoot.setState(chatId, new ChoseState(telegramBoot));
    }
}
