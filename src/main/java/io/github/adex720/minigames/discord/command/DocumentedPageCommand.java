package io.github.adex720.minigames.discord.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.gameplay.manager.command.PageMovementManager;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * A command that can be navigated with 'previous'- and 'next'-buttons.
 * The button format is page-type-pageNumber-userId-optionalArguments.
 * <p>
 * The content of the pages is loaded from a json-file from 'resources/documentation'.
 *
 * @author adex720
 * @see PageMovementManager
 */
public interface DocumentedPageCommand extends PageCommand {

    String getJsonFileName();

    default MessageEmbed.Field[][] loadPagesContent(MinigamesBot bot) {
        JsonArray pagesJson = bot.getResourceJson("documentation/" + getJsonFileName()).getAsJsonArray();

        int pageCount = pagesJson.size();
        MessageEmbed.Field[][] pages = new MessageEmbed.Field[pageCount][];

        for (int i = 0; i < pageCount; i++) {
            JsonArray fieldsJson = pagesJson.get(i).getAsJsonArray();
            int fieldCount = fieldsJson.size();

            MessageEmbed.Field[] fields = new MessageEmbed.Field[fieldCount];
            for (int i2 = 0; i2 < fieldCount; i2++) {
                JsonObject fieldJson = fieldsJson.get(i2).getAsJsonObject();

                String question = JsonHelper.getString(fieldJson, "question");
                String answer = JsonHelper.getString(fieldJson, "answer");

                fields[i2] = new MessageEmbed.Field(question, answer, false);
            }

            pages[i] = fields;
        }

        return pages;
    }
}
