package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Subscriber;
import play.station.bot.util.TelegramBoot;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final TelegramBoot telegramBoot;

    public void send(Collection<Subscriber> subscribers, String message) {
        for (Subscriber subscriber : subscribers) {
            telegramBoot.sendMessage(subscriber.getChatId(), message);
        }
    }
}
