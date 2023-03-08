package play.station.bot.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class Utils {

    public static Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        }
        return update.getCallbackQuery().getMessage().getChatId();
    }

}
