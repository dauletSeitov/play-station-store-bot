package play.station.bot.states;

import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.model.entities.Product;
import play.station.bot.util.TelegramBoot;
import play.station.bot.util.Utils;

import java.util.List;

public class UnsubscribeState extends State {
    private final List<Product> productList;

    public UnsubscribeState(TelegramBoot telegramBoot, List<Product> productList) {
        super(telegramBoot);
        this.productList = productList;
    }


    @Override
    public void go(Update update) {
        boolean unsubscribed = telegramBoot.unsubscribe(update, productList);
        if (unsubscribed) {
            telegramBoot.setState(Utils.getChatId(update), new StartState(telegramBoot));
        }
    }
}
