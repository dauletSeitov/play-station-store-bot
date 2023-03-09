package play.station.bot.states;

import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.model.entities.Product;
import play.station.bot.TelegramBoot;
import play.station.bot.util.Utils;

import java.util.List;

public class SearchState extends State {
    public SearchState(TelegramBoot telegramBoot) {
        super(telegramBoot);
    }

    @Override
    public void go(Update update) {
        Long chatId = Utils.getChatId(update);
        List<Product> list = telegramBoot.search(chatId, update.getMessage().getText());
        if (list.isEmpty()) return;
        telegramBoot.setState(chatId, new SubscribeState(telegramBoot, list));
    }

}
