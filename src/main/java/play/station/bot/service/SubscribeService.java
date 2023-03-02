package play.station.bot.service;

import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Subscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SubscribeService {

    private final Map<String, List<Subscriber>> map = new HashMap<>();

    public void saveSubscriber(String productId, Subscriber subscriber) {
        map.merge(productId, new ArrayList<>() {{
            add(subscriber);
        }}, (a, b) -> {
            a.addAll(b);
            return a;
        });
    }


    public List<String> getProductIdsByChatId(Long chatId) {
        List<String> result = new ArrayList<>();

        map.forEach((key, val) -> {
            if (val.stream().anyMatch(itm -> itm.getChatId().equals(chatId))) result.add(key);
        });
        return result;
    }

    public void unsubscribe(Long chatId, String productId) {
        List<Subscriber> rest = map.get(productId).stream().filter(itm -> !itm.getChatId().equals(chatId)).collect(Collectors.toList());
        map.put(productId, rest);
    }
}
