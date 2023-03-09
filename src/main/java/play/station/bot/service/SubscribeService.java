package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;
import play.station.bot.model.entities.Subscriber;
import play.station.bot.repository.ProductRepository;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final ProductRepository productRepository;

    @Transactional
    public void unsubscribe(Long chatId, String productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("no such product"));
        Set<Subscriber> subscribers = product.getSubscribers().stream().filter(itm -> !chatId.equals(itm.getChatId())).collect(Collectors.toSet());
        product.setSubscribers(subscribers);
        productRepository.save(product);
    }

}
