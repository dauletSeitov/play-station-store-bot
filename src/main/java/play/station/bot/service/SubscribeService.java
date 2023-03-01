package play.station.bot.service;

import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Subscriber;

import java.util.ArrayList;
import java.util.List;

@Service
public class SubscribeService {

    List<Subscriber> list = new ArrayList<>();
    public List<Subscriber> getAllSubscribers(){
        return list;
    }

    public void subscribe(Long chatId, String pair) {
        list.add(new Subscriber(chatId));
    }
}
