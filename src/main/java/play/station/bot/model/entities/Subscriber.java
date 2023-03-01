package play.station.bot.model.entities;

import lombok.Data;
import lombok.RequiredArgsConstructor;

//@Entity
@Data
@RequiredArgsConstructor
public class Subscriber {
    private final Long chatId;
}
