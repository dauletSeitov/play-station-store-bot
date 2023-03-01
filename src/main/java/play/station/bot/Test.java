package play.station.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {

        String query = "last of us";
        String url = "https://store.playstation.com/es-es/search/" + query;
        Document doc = Jsoup.connect(url).get();
        Elements searchResultsGrid = doc.getElementsByAttributeValue("data-qa", "search-results-grid");

        Elements searchResults = searchResultsGrid.select("ul").select("li");

        int i = 0;
        for (Element element : searchResults) {
            String attr = element.select("a").attr("data-telemetry-meta");

            JsonNode jsonNode = new ObjectMapper().readTree(attr);

            System.out.println(jsonNode.get("name").asText());
            System.out.println(jsonNode.get("id").asText());
            System.out.println(jsonNode.get("price").asText());

            Elements discount = element.select("s");
            if (!discount.isEmpty()) {
                System.out.println(discount.text());
            }

            System.out.println(element.select("img").last().attr("src"));
            System.out.println("-----------------");
            i++;
        }
        System.out.println();


    }


}
