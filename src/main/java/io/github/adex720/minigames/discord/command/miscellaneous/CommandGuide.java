package io.github.adex720.minigames.discord.command.miscellaneous;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.PageCommand;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Date;

/**
 * @author adex720
 */
public class CommandGuide extends PageCommand {

    private MessageEmbed.Field[][] CONTENT;

    public CommandGuide(MinigamesBot bot) {
        super(bot, "guide", "Teaches you how to use the bot.", CommandCategory.MISCELLANEOUS);
    }

    private MessageEmbed.Field[][] loadPagesContent() {
        JsonArray pagesJson = bot.getResourceJson("guide").getAsJsonArray();

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

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        OptionMapping optionMapping = event.getOption("page");
        int page = 1;
        if (optionMapping != null) page = optionMapping.getAsInt();

        sendPage(Replyable.from(event), ci, page);
        return true;
    }

    @Override
    public void onPageMove(ButtonInteractionEvent event, CommandInfo ci, int page, String[] args) {
        sendPage(Replyable.edit(event), ci, page);
    }

    public void sendPage(Replyable replyable, CommandInfo commandInfo, int page) {
        long userId = commandInfo.authorId();
        MessageEmbed message = getPage(commandInfo, page);
        Button buttonPrevious = getButtonForPage(userId,page - 1, "previous", page == 1);
        Button buttonNext = getButtonForPage(userId,page + 1, "next", page == CONTENT.length);

        replyable.reply(message, buttonPrevious, buttonNext);
    }

    public MessageEmbed getPage(CommandInfo commandInfo, int page) {
        User author = commandInfo.author();

        EmbedBuilder embedBuilder = new EmbedBuilder();

        for (MessageEmbed.Field field : CONTENT[page - 1]) {
            embedBuilder.addField(field);
        }

        return embedBuilder
                .setTitle("GUIDE (Page " + (page) + "/" + CONTENT.length + ")")
                .setColor(Util.getColor(author.getIdLong()))
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    @Override
    protected SlashCommandData createCommandData() {
        CONTENT = loadPagesContent();

        return super.createCommandData()
                .addOptions(new OptionData(OptionType.STRING, "page", "Page of guide", false)
                        .addChoices(getChoices()));
    }

    private Command.Choice[] getChoices() {
        int length = CONTENT.length;
        Command.Choice[] choices = new Command.Choice[length];

        for (int i = 1; i <= length; i++) {
            choices[i - 1] = new Command.Choice(Integer.toString(i), i);
        }

        return choices;
    }
}
