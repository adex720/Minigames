package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.settings.Setting;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.Date;

public class CommandSettings extends Command {

    private final ArrayList<Setting> settings;

    public CommandSettings(MinigamesBot bot) {
        super(bot, "settings", "Changes your personal settings", CommandCategory.USER);
        requiresProfile();

        settings = new ArrayList<>();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        OptionMapping settingMapping = event.getOption("setting");

        if (settingMapping == null) {
            event.getHook().sendMessageEmbeds(getSettings(ci)).queue();
            return true;
        }

        OptionMapping valueMapping = event.getOption("value");
        if (valueMapping == null) {
            event.getHook().sendMessage("Please include the new value!").queue();
            return true;
        }

        String reply = set(ci, (int) settingMapping.getAsLong(), valueMapping.getAsBoolean());
        event.getHook().sendMessage(reply).queue();
        return true;
    }

    public MessageEmbed getSettings(CommandInfo ci) {
        Profile profile = ci.profile();
        User author = ci.author();

        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("SETTINGS")
                .setColor(Util.getColor(author.getIdLong()));

        for (Setting setting : settings) {
            embedBuilder.addField(setting.name(), setting.description() + "\nCurrent value: " + profile.getSetting(setting), true);
        }

        return embedBuilder
                .setFooter(author.getName(), author.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();
    }

    public String set(CommandInfo ci, int settingId, boolean value) {
        Profile profile = ci.profile();
        Setting setting = profile.setSetting(settingId, value);

        return "Set " + setting.name() + " to " + value + "!";
    }

    public void addSetting(Setting setting) {
        settings.add(setting);
    }

    @Override
    protected CommandData createCommandData() {
        OptionData settings = new OptionData(OptionType.INTEGER, "setting", "Setting to change", false);

        for (Setting setting : bot.getSettingsList().getAll()) {
            settings.addChoice(setting.name(), setting.id());
            this.settings.add(setting);
        }

        return super.createCommandData()
                .addOptions(settings)
                .addOption(OptionType.BOOLEAN, "value", "Value to set", false);
    }
}
