package play.station.bot.states;

import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.model.entities.Product;
import play.station.bot.TelegramBoot;
import play.station.bot.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class SubscribeState extends State {
    private List<Product> productList = new ArrayList<>();

    public SubscribeState(TelegramBoot telegramBoot, List<Product> productList) {
        super(telegramBoot);
        this.productList = productList;
    }

    @Override
    public void go(Update update) {

        boolean subscribed = telegramBoot.subscribe(update, productList);
        if (subscribed) {
            telegramBoot.setState(Utils.getChatId(update), new StartState(telegramBoot));
        }
    }

}
