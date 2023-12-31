package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;
import play.station.bot.model.entities.Subscriber;
import play.station.bot.repository.ProductRepository;
import play.station.bot.repository.SubscribeRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SubscribeRepository subscribeRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Transactional
    public void subscribe(Product product, Long chatId, String userName, String name) {
        log.info("subscribe product: {} chatId :{} userName: {} name: {}", product, chatId, userName, name);
        Subscriber subscriber = Subscriber.builder()
                .chatId(chatId)
                .login(userName)
                .name(name)
                .build();
        Subscriber savedSubscriber = subscribeRepository.save(subscriber);

        Optional<Product> productOpt = productRepository.findById(product.getId());
        if (productOpt.isEmpty()) {
            product.setSubscribers(Set.of(savedSubscriber));
            productRepository.save(product);
        } else {
            product = productOpt.get();
            product.getSubscribers().add(savedSubscriber);
            productRepository.save(product);
        }
    }

    public List<Product> getSubscribedProducts(Long chatId) {
        return productRepository.findSubscribedProductsByChatId(chatId);
    }
}
