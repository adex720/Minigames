package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.DocumentedPageCommand;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.replyable.Replyable;
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
public class CommandGuildHelp extends Subcommand implements DocumentedPageCommand {

    private MessageEmbed.Field[][] CONTENT;

    public CommandGuildHelp(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "help", "Displays information on how guilds work.", CommandCategory.GUILD);
        requiresProfile();

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
                .setTitle("GUILDS HELP (Page " + (page) + "/" + CONTENT.length + ")")
                .setColor(Util.getColor(author.getIdLong()))
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }


    public String getPageName() {
        return "guildhelp";
    }

    @Override
    public String getJsonFileName() {
        return "guild";
    }

    @Override
    protected SlashCommandData createCommandData() {
        CONTENT = loadPagesContent(bot);

        return super.createCommandData()
                .addOptions(new OptionData(OptionType.STRING, "page", "Page of help message", false)
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
