package play.station.bot.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import play.station.bot.model.entities.Product;
import play.station.bot.model.entities.Subscriber;
import play.station.bot.service.ProductService;
import play.station.bot.service.SearchService;
import play.station.bot.service.SubscribeService;
import play.station.bot.states.StartState;
import play.station.bot.states.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBoot extends TelegramLongPollingBot {

    private final SubscribeService subscribeService;
    private final ProductService productService;
    private final SearchService searchService;

    private final String itemDescriptionPattern = "<b>${num})</b> <b>${price}</b> <s>${actualPrice}</s> <a href='${url}'>${displayUrl}</a>";
    @Value("${bot.bot-name}")
    private String botName;
    @Value("${bot.bot-key}")
    private String botKey;

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botKey;
    }

    private final Map<Long, State> chatStateMap = new HashMap<>();

    public void setState(Long chatId, State state) {
        chatStateMap.put(chatId, state);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        log.info("got message: {}", text);

        State state = chatStateMap.getOrDefault(chatId, new StartState(this));

        switch (update.getMessage().getText()) {
            case "exit":
                state.cancel(chatId);
                break;
            case "search":
                state.search(update);
                break;
            case "subscriptions":
                state.subscriptions(update);
                break;
            case "unsubscribe":
                state.unsubscribe(update);
                break;
            default:
                state.go(update);


        }
    }


    public void sendMessage(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);
        try {
            execute(sendMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void sendImage(Long chatId, Product product, int i) {

        try {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setParseMode("html");
            sendPhoto.setCaption(createCaption(product, i));
            sendPhoto.setPhoto(new InputFile(product.getImage()));
            execute(sendPhoto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            sendMessage(chatId, createCaption(product, i));
        }

    }

    public void sendImages(Long chatId, List<Product> productList) {
        for (int i = 0; i < productList.size() && i < 10; i++) {
            sendImage(chatId, productList.get(i), i);
        }
    }

    private String createCaption(Product product, int i) {
        return itemDescriptionPattern.replace("${url}", product.getUrl())
                .replace("${displayUrl}", product.getName())
                .replace("${price}", product.getPrice())
                .replace("${num}", "" + i)
                .replace("${actualPrice}", product.getActualPrice());
    }
// actions

    public void exit(Long chatId) {
        sendMessage(chatId, "Pleas enter command");
    }

    public void showMessageEnterPhrase(Update update) {
        sendMessage(update.getMessage().getChatId(), "Pleas enter phrase to search");
    }

    public List<Product> search(Long chatId, String name) {
        List<Product> productList = searchService.search(name);
        if (productList.isEmpty()) {
            sendMessage(chatId, "nothing was found, please try other phrase");
        } else {
            sendImages(chatId, productList);
            sendMessage(chatId, "please type chosen games number to subscribe. Or type exit to cancel");
        }
        return productList;
    }


    public boolean subscribe(Update update, List<Product> productList) {

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        Product product;

        try {
            int i = Integer.parseInt(text);
            if (i >= 10) throw new RuntimeException("more than max");
            product = productList.get(i);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendMessage(chatId, String.format("No such product with number (%s). Please chose other number or type exit to cancel", text));
            return false;
        }

        productService.saveProduct(product);
        Subscriber subscriber = new Subscriber(chatId, update.getMessage().getFrom().getUserName());
        subscribeService.saveSubscriber(product.getId(), subscriber);
        sendMessage(chatId, String.format("You subscribed to product (%s). As soon as the price changes we will let you know. To subscribe to other products. Please enter next command.", product.getName()));
        return true;
    }

    public List<Product> getSubscribedProducts(Update update) {

        Long chatId = update.getMessage().getChatId();
        List<String> productIds = subscribeService.getProductIdsByChatId(chatId);
        List<Product> productList = productService.getProductsByIds(productIds);

        if (productList.isEmpty()) {
            sendMessage(chatId, "You dont have any subscription");
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < productList.size(); i++) {
                sb.append(createCaption(productList.get(i), i)).append("\n");
            }
            sendMessage(chatId, sb.toString());
            sendMessage(chatId, "please type chosen games number to unsubscribe. Or type exit to cancel");
        }
        return productList;
    }

    public boolean unsubscribe(Update update, List<Product> productList) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        Product product;

        try {
            int i = Integer.parseInt(text);
            product = productList.get(i);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendMessage(chatId, String.format("No such product with number (%s). Please chose other number or type exit to cancel", text));
            return false;
        }
        subscribeService.unsubscribe(update.getMessage().getChatId(), product.getId());
        sendMessage(chatId, String.format("You successfully unsubscribed from the product (%s)", product.getName()));
        return true;
    }
}