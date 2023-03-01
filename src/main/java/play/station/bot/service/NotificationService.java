package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import play.station.bot.util.TelegramBoot;
import play.station.bot.model.entities.Subscriber;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SubscribeService subscribeService;
    private final TelegramBoot telegramBoot;
    public void send(String body) {
        for (Subscriber subscriber : subscribeService.getAllSubscribers()) {
            telegramBoot.sendMessage(body, subscriber.getChatId());
        }
    }
}
