package io.github.adex720.minigames.discord.command.miscellaneous;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.DocumentedPageCommand;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Date;

/**
 * @author adex720
 */
public class CommandGuide extends Command implements DocumentedPageCommand {

    private MessageEmbed.Field[][] CONTENT;

    public CommandGuide(MinigamesBot bot) {
        super(bot, "guide", "Teaches you how to use the bot.", CommandCategory.MISCELLANEOUS);
        registerPageId(bot);
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
        event.deferEdit().queue();
        sendPage(Replyable.edit(event), ci, page);
    }

    @Override
    public String getPageName() {
        return name;
    }

    @Override
    public String getJsonFileName() {
        return "guide";
    }

    public void sendPage(Replyable replyable, CommandInfo commandInfo, int page) {
        long userId = commandInfo.authorId();
        MessageEmbed message = getPage(commandInfo, page);
        Button buttonPrevious = getButtonForPage(userId, page - 1, "previous", page == 1);
        Button buttonNext = getButtonForPage(userId, page + 1, "next", page == CONTENT.length);

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
        CONTENT = loadPagesContent(bot);

        return super.createCommandData()
                .addOptions(new OptionData(OptionType.STRING, "page", "Page of guide", false)
                        .addChoices(getChoices()));
    }

    private net.dv8tion.jda.api.interactions.commands.Command.Choice[] getChoices() {
        int length = CONTENT.length;
        net.dv8tion.jda.api.interactions.commands.Command.Choice[] choices = new net.dv8tion.jda.api.interactions.commands.Command.Choice[length];

        for (int i = 1; i <= length; i++) {
            choices[i - 1] = new net.dv8tion.jda.api.interactions.commands.Command.Choice(Integer.toString(i), i);
        }

        return choices;
    }
}
