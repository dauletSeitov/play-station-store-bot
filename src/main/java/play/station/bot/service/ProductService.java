package play.station.bot.service;

import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final Map<String, Product> map = new HashMap<>();

    public void saveProduct(Product product) {
        map.putIfAbsent(product.getId(), product);
    }

    public List<Product> getProductsByIds(List<String> productIds) {
        return productIds.stream().map(map::get).collect(Collectors.toList());
    }

    public List<Product> getAll() {
        return new ArrayList<>(map.values());
    }
}
