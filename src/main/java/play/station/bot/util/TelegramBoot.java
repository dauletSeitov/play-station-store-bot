package play.station.bot.util;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import play.station.bot.model.entities.Product;
import play.station.bot.service.SearchService;
import play.station.bot.service.SubscribeService;
import play.station.bot.states.SearchState;
import play.station.bot.states.State;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBoot extends TelegramLongPollingBot {

    private final SubscribeService subscribeService;
    private final SearchService searchService;

    private final String itemDescriptionPattern = "<b>${price}</b> <s>${actualPrice}</s> <a href='${url}'>${displayUrl}</a>";
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


        State state = chatStateMap.getOrDefault(chatId, new SearchState(this));

        if ("exit".equalsIgnoreCase(update.getMessage().getText())) {
            state.cancel(chatId);
        } else {
            state.go(chatId, text);
        }

//        String text = update.getMessage().getText();
//        String[] pairs = text.split(":");
//
//        if ("subscribe".equals(pairs[0])) {
//            subscribeService.subscribe(update.getMessage().getChatId(), pairs[0]);
//            sendMessage("subscribed", update.getMessage().getChatId());
//        }


    }


    public void sendMessage(String message, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }

    @SneakyThrows
    public void sendImage(Long chatId, Product product) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setParseMode("html");
        sendPhoto.setCaption(createCaption(product));
        sendPhoto.setPhoto(new InputFile());
        execute(sendPhoto);
        log.info("the photo was sent successfully to the chat {}", chatId);
    }

    private String createCaption(Product product) {
        return itemDescriptionPattern.replace("${url}", product.getUrl())
                .replace("${displayUrl}", product.getName())
                .replace("${price}", product.getPrice())
                .replace("${actualPrice}", product.getActualPrice());
    }

    @SneakyThrows
    public void sendImages(Long chatId, List<Product> productList) {
        if (productList.size() == 1) {
            sendImage(chatId, productList.get(0));
            return;
        }
        if (productList.size() > 10) {
            productList = productList.subList(0, 10);
        }
        SendMediaGroup group = new SendMediaGroup();
        group.setChatId(chatId);

        List<InputMedia> medias = new ArrayList<>();
        for (Product product : productList) {
            InputMedia media = new InputMediaPhoto();
            media.setMedia(product.getImage());
            media.setMediaName(product.getName());
            media.setCaption(createCaption(product));
            media.setParseMode(ParseMode.HTML);
            medias.add(media);
        }
        group.setMedias(medias);
        execute(group);
        log.info("group of images successfully sent to the chat {}", chatId);
    }


    public List<Product> search(Long chatId, String name) {
        List<Product> productList = searchService.search(name);
        if (productList.isEmpty()) {
            sendMessage("nothing was found, please try other phrase", chatId);
        } else {
            sendImages(chatId, productList);
            sendMessage("please type chosen games number to subscribe. Or type exit to cancel", chatId);
        }
        return productList;
    }

    public void exit(Long chatId) {
        sendMessage("Pleas enter phrase to search", chatId);
    }
}