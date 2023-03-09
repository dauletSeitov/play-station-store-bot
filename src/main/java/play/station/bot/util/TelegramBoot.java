package play.station.bot.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import play.station.bot.model.entities.Product;
import play.station.bot.service.ProductService;
import play.station.bot.service.SearchService;
import play.station.bot.service.SubscribeService;
import play.station.bot.states.Actions;
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
    private final Actions actions;

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

        String text = null;
        if (update.hasMessage()) {
            text = update.getMessage().getText();
        } else if (StringUtils.hasLength(update.getCallbackQuery().getData())) {
            text = update.getCallbackQuery().getData();

        }
        if (!StringUtils.hasLength(text)) {
            return;
        }
        Long chatId = Utils.getChatId(update);

        log.info("got message: {}", text);

        State state = chatStateMap.getOrDefault(chatId, new StartState(this));

        switch (text) {
            case "/cancel":
            case "/cancelButton":
                state.cancel(chatId);
                break;
            case "/search":
            case "/searchButton":
                state.search(update);
                break;
            case "/subscriptions":
            case "/subscriptionsButton":
                state.subscriptions(update);
                break;
            case "/unsubscribe":
            case "/unsubscribeButton":
                state.unsubscribe(update);
                break;
            default:
                state.go(update);
        }
    }

    public void sendMessage(Long chatId, String message) {
        sendMessage(chatId, message, List.of());
    }

    public void sendMessage(Long chatId, String message, List<KeyboardButton> buttons) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setParseMode(ParseMode.HTML);

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.addAll(buttons);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(List.of(keyboardFirstRow));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);

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

    public void cancel(Long chatId) {
        sendMessage(chatId, "Please choose command", List.of(actions.getSearch(), actions.getSubscriptions()));
    }

    public void showMessageEnterPhrase(Update update) {
        sendMessage(Utils.getChatId(update), "Please enter phrase to search", List.of(actions.getCancel()));
    }

    public List<Product> search(Long chatId, String name) {
        List<Product> productList = searchService.search(name);
        if (productList.isEmpty()) {
            sendMessage(chatId, "Nothing was found, please try other phrase", List.of(actions.getCancel()));
        } else {
            sendImages(chatId, productList);
            sendMessage(chatId, "Please type chosen games number to subscribe", List.of(actions.getCancel()));
        }
        return productList;
    }


    public boolean subscribe(Update update, List<Product> productList) {

        Long chatId = Utils.getChatId(update);
        String text = update.getMessage().getText();
        Product product;

        try {
            int i = Integer.parseInt(text);
            if (i >= 10) throw new RuntimeException("more than max");
            product = productList.get(i);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendMessage(chatId, String.format("No such product with number (%s). Please choose another number", text), List.of(actions.getCancel()));
            return false;
        }

        productService.subscribe(product, chatId, update.getMessage().getFrom().getUserName(),
                update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());

        sendMessage(chatId,
                String.format("You subscribed to product (%s). As soon as the price changes we will let you know. To subscribe to other products. Please enter next command",
                        product.getName()),
                List.of(actions.getSearch(), actions.getSubscriptions()));
        return true;
    }

    public List<Product> getSubscribedProducts(Update update) {

        Long chatId = Utils.getChatId(update);
        List<Product> productList = productService.getSubscribedProducts(chatId);

        if (productList.isEmpty()) {
            sendMessage(chatId, "You don't have any subscription", List.of(actions.getSearch(), actions.getSubscriptions()));
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < productList.size(); i++) {
                sb.append(createCaption(productList.get(i), i)).append("\n");
            }
            sendMessage(chatId, sb.toString());
            sendMessage(chatId, "Please type chosen games number to unsubscribe", List.of(actions.getCancel()));
        }
        return productList;
    }

    public boolean unsubscribe(Update update, List<Product> productList) {
        Long chatId = Utils.getChatId(update);
        String text = update.getMessage().getText();
        Product product;

        try {
            int i = Integer.parseInt(text);
            product = productList.get(i);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            sendMessage(chatId, String.format("No such product with numbers (%s). Please choose another number", text), List.of(actions.getCancel()));
            return false;
        }
        subscribeService.unsubscribe(Utils.getChatId(update), product.getId());
        sendMessage(chatId, String.format("You successfully unsubscribed from the product (%s)", product.getName()), List.of(actions.getSearch(), actions.getSubscriptions()));
        return true;
    }
}