package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;
import play.station.bot.model.entities.Subscriber;
import play.station.bot.repository.ProductRepository;
import play.station.bot.repository.SubscribeRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final SubscribeRepository subscribeRepository;
    private final ProductRepository productRepository;

    public void unsubscribe(Long chatId, String productId) {
//        List<Subscriber> rest = map.get(productId).stream().filter(itm -> !itm.getChatId().equals(chatId)).collect(Collectors.toList());
//        map.put(productId, rest);
    }

}
