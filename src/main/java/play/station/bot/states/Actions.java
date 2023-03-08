package play.station.bot.states;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

@Service
@Getter
public class Actions {
    private final InlineKeyboardButton search = InlineKeyboardButton.builder()
            .text("/search")
            .callbackData("/searchButton")
            .build();
    private final InlineKeyboardButton cancel = InlineKeyboardButton.builder()
            .text("/cancel")
            .callbackData("/cancelButton")
            .build();

    private final InlineKeyboardButton subscriptions = InlineKeyboardButton.builder()
            .text("/subscriptions")
            .callbackData("/subscriptionsButton")
            .build();
}
