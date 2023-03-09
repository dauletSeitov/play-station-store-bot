package play.station.bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;

import javax.transaction.Transactional;
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
    private final SearchService searchService;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void doScan() throws IOException, InterruptedException {

        List<Product> productList = productService.getAll();
        log.info("job started at: {} for products: {}", LocalDateTime.now(), productList.size());

        for (Product product : productList) {
            List<Product> search = searchService.search(product.getName());
            Optional<Product> productOpt = search.stream().filter(itm -> itm.getId().equals(product.getId())).findFirst();
            if (productOpt.isPresent() && !productOpt.get().getPrice().equals(product.getPrice())) {
                log.info("price of product: {} changed", product);
                notify(product, productOpt.get());
            }
            Thread.sleep(1000);
        }
    }

    private void notify(Product oldProduct, Product currentProduct) {
        log.info("notify subscribers: {}", oldProduct.getSubscribers().size());
        notificationService.send(oldProduct.getSubscribers(), String.format("Product (%s) is available by price %s. Previous price: %s", oldProduct.getName(), currentProduct.getPrice(), oldProduct.getPrice()));
    }
}
