package play.station.bot.service;

import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {

    private Map<String, Product> map = new HashMap<>();

//    public String getPrice(String name, String price) {
//        return map.getOrDefault(name, "");
//    }

    public void saveProduct(Product product) {
        map.putIfAbsent(product.getId(), product);
    }
}
