package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;
import play.station.bot.model.entities.Subscriber;
import play.station.bot.repository.ProductRepository;
import play.station.bot.repository.SubscribeRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SubscribeRepository subscribeRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Transactional
    public void subscribe(Product product, Long chatId, String userName) {

        Subscriber subscriber = Subscriber.builder().chatId(chatId).login(userName).build();
        Subscriber savedSubscriber = subscribeRepository.save(subscriber);

        Optional<Product> productOpt = productRepository.findById(product.getId());
        if (productOpt.isEmpty()) {
            product.setSubscribers(Set.of(savedSubscriber));
            product = productRepository.save(product);
        } else {
            product = productOpt.get();
            product.getSubscribers().add(savedSubscriber);
            product = productRepository.save(product);
        }


    }

    public List<Product> getSubscribedProducts(Long chatId) {
        return productRepository.findSubscribedProductsByChatId(chatId);
    }
}
