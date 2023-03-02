package play.station.bot.states;

import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.model.entities.Product;
import play.station.bot.util.TelegramBoot;

import java.util.ArrayList;
import java.util.List;

public class ChoseState extends State {
    private List<Product> productList = new ArrayList<>();

    public ChoseState(TelegramBoot telegramBoot, List<Product> productList) {
        super(telegramBoot);
        this.productList = productList;
    }

    @Override
    public void cancel(Long chatId) {
        telegramBoot.exit(chatId);
        telegramBoot.setState(chatId, new SearchState(telegramBoot));
    }

    @Override
    public void go(Update update) {

        boolean subscribed = telegramBoot.subscribe(update, productList);
        if (subscribed) {
            telegramBoot.setState(update.getChannelPost().getChatId(), new SearchState(telegramBoot));
        }
    }
}
