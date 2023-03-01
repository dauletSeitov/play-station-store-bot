package play.station.bot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {
    private final RestTemplate restTemplate;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    private final ProductService gameService;

    //@Scheduled(fixedRate = 5000)
    public void doScan() throws IOException {
        log.info("The time is now {}", LocalDateTime.now());

        String s = Files.readString(Path.of("/home/phantom/IdeaProjects/play-station-store-bot/1.txt"));

        String fooResourceUrl = "https://web.np.playstation.com/api/graphql/v1//op?operationName=queryRetrieveTelemetryDataPDPProduct&variables=%7B%22conceptId%22%3Anull%2C%22productId%22%3A%22EP4433-CUSA00265_00-MINECRAFTPS40001%22%7D&extensions=%7B%22persistedQuery%22%3A%7B%22version%22%3A1%2C%22sha256Hash%22%3A%22163ce11323f3618e7a2fb5ef467db2f7f02ddade88218a83b5b414f1f65cfdce%22%7D%7D";


        URL url = new URL(fooResourceUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        JsonNode jsonNode = objectMapper.readTree(content.toString());
        JsonNode jsonNode1 = jsonNode.get("data").get("productRetrieve").get("webctas");
        for (JsonNode price : jsonNode1) {
            if(price.get("").equals("")){
                gameService.addGame("minecraft", "");
            }
            //System.out.println(objNode);
        }

        notificationService.send(content.toString());
    }
}
