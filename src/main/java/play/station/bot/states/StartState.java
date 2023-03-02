package play.station.bot.states;

import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.model.entities.Product;
import play.station.bot.util.TelegramBoot;

import java.util.List;

public class StartState extends State {
    public StartState(TelegramBoot telegramBoot) {
        super(telegramBoot);
    }

    @Override
    public void cancel(Long chatId) {
        telegramBoot.exit(chatId);
    }

    @Override
    public void go(Update update) {
        Long chatId = update.getMessage().getChatId();
        List<Product> list = telegramBoot.search(chatId, update.getMessage().getText());
        if (list.isEmpty()) return;
        telegramBoot.setState(chatId, new ChoseState(telegramBoot, list));
    }
}
