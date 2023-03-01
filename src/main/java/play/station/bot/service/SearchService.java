package play.station.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import play.station.bot.model.entities.Product;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final ObjectMapper objectMapper;
    private final String baseUrl = "https://store.playstation.com/";
    private final String url = "https://store.playstation.com/es-es/search/";

    @SneakyThrows
    public List<Product> search(String name) {
        List<Product> productList = new ArrayList<>();
        Document doc = Jsoup.connect(baseUrl + "es-es/search/" + name).get();
        Elements searchResultsGrid = doc.getElementsByAttributeValue("data-qa", "search-results-grid");

        Elements searchResults = searchResultsGrid.select("ul").select("li");

        for (Element element : searchResults) {
            String attr = element.select("a").attr("data-telemetry-meta");

            JsonNode jsonNode = objectMapper.readTree(attr);

            Elements discount = element.select("s");

            productList.add(Product.builder()
                    .id(jsonNode.get("id").asText())
                    .name(jsonNode.get("name").asText())
                    .price(jsonNode.get("price").asText())
                    .actualPrice(discount.isEmpty() ? "" : discount.text())
                    .image(element.select("img").last().attr("src"))
                    .url(baseUrl + "/es-es/product/" + jsonNode.get("id").asText())
                    .build());
        }
        return productList;
    }
}
