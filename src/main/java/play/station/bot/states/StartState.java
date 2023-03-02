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
    public void search(Update update) {
        telegramBoot.showMessageEnterPhrase(update);
        telegramBoot.setState(update.getMessage().getChatId(), new SearchState(telegramBoot));
    }

    @Override
    public void subscriptions(Update update) {
        List<Product> productList = telegramBoot.getSubscribedProducts(update);
        if (!productList.isEmpty()) {
            telegramBoot.setState(update.getMessage().getChatId(), new UnsubscribeState(telegramBoot, productList));
        }
    }

}
