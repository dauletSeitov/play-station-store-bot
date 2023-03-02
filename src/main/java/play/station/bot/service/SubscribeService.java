package play.station.bot.service;

import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Subscriber;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscribeService {

    private Map<String, Subscriber> map = new HashMap<>();

    public void saveSubscriber(String productId, Subscriber subscriber) {
        map.put(productId, subscriber);
    }
}
