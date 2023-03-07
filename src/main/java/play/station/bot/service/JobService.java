package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;
import play.station.bot.model.entities.Subscriber;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final NotificationService notificationService;
    private final ProductService productService;
    private final SubscribeService subscribeService;
    private final SearchService searchService;

    @Scheduled(fixedRate = 60000)//TODO
    public void doScan() throws IOException, InterruptedException {
        log.info("The time is now {}", LocalDateTime.now());

        List<Product> productList = productService.getAll();

        for (Product product : productList) {
            List<Product> search = searchService.search(product.getName());
            Optional<Product> productOpt = search.stream().filter(itm -> itm.getId().equals(product.getId())).findFirst();
            if (productOpt.isPresent() && !productOpt.get().getPrice().equals(product.getPrice())) {
                notify(product, productOpt.get());
            }
            System.out.println();
//            Thread.sleep(1000); TODO 
        }


    }

    private void notify(Product oldProduct, Product currentProduct) {
        List<Subscriber> subscribers = subscribeService.getByProductId(oldProduct.getId());
        notificationService.send(subscribers, String.format("Product (%s) is available by price %s. Previous price: %s", oldProduct.getName(), currentProduct.getPrice(), oldProduct.getPrice()));
    }
}
