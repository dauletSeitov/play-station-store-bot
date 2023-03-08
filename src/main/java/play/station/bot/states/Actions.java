package play.station.bot.states;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;

@Service
@Getter
public class Actions {

    private final KeyboardButton search = KeyboardButton.builder().text("/search").build();
    private final KeyboardButton cancel = KeyboardButton.builder().text("/cancel").build();
    private final KeyboardButton subscriptions = KeyboardButton.builder().text("/subscriptions").build();
}
