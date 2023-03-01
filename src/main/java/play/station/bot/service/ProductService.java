package play.station.bot.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductService {

    private Map<String, String> map = new HashMap<>();

    public void addGame(String name, String price) {
        map.put(name, price);
    }

    public String getPrice(String name, String price) {
        return map.getOrDefault(name, "");
    }
}
