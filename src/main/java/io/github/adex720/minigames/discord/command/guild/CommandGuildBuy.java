package io.github.adex720.minigames.discord.command.guild;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.gameplay.guild.Guild;
import io.github.adex720.minigames.gameplay.guild.shop.GuildPerk;
import io.github.adex720.minigames.gameplay.guild.shop.GuildPerkList;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Collection;

/**
 * @author adex720
 */
public class CommandGuildBuy extends Subcommand {

    public CommandGuildBuy(MinigamesBot bot) {
        super(bot, bot.getCommandManager().parentCommandGuild, "buy", "Purchases an upgrade from the guild shop.", CommandCategory.GUILD);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        Guild guild = ci.guild();
        if (!ci.isInGuild()) {
            event.getHook().sendMessage("You are not in a guild!").setEphemeral(true).queue();
            return true;
        }

        long userId = ci.authorId();
        if (!guild.isElderOrOwner(userId)) {
            event.getHook().sendMessage("Only guild owner and elders can purchase something from the guild shop!").setEphemeral(true).queue();
            return true;
        }

        GuildPerkList perkList = guild.getPerkList();
        int balance = /*guild.getCoins();*/ 5000;

        int perkId = event.getOption("perk").getAsInt();

        OptionMapping upgradeCountOptionMapping = event.getOption("count");
        int price = perkList.getPrice(perkId);

        if (perkList.isMaxed(perkId)) {
            event.getHook().sendMessage("The perk is already maxed!").queue();
            return true;
        }

        if (upgradeCountOptionMapping == null) {
            if (price > balance) {
                event.getHook().sendMessage("You can't afford the upgrade!").queue();
                return true;
            }

            perkList.upgrade(perkId);
            guild.removeCoins(price);
            event.getHook().sendMessage("You upgraded " + perkList.getName(perkId) + " to level " + perkList.getLevel(perkId) +
                    ". You have " + (balance - price) + " coins left.").queue();
            return true;
        }

        int count = upgradeCountOptionMapping.getAsInt();

        if (count < 1) {
            event.getHook().sendMessage("You need to purchase at least one level!").queue();
            return true;
        }

        int bought = 0;
        while (balance > price) { // Check if more can be bought
            if (count == 0) break;

            perkList.upgrade(perkId);
            guild.removeCoins(price);
            balance -= price;

            bought++;
            count--;

            if (perkList.isMaxed(perkId)) break;
            price = perkList.getPrice(perkId);
        }

        String levels = bought != 1 ? " levels of **" : " level of **";
        event.getHook().sendMessage("You bought " + bought + levels + perkList.getName(perkId) + "**. It's now on level "
                + perkList.getLevel(perkId) + ". You have " + (balance - price) + " coins left.").queue();
        return true;
    }

    @Override
    protected SubcommandData getSubcommandData() {
        return super.getSubcommandData()
                .addOptions(new OptionData(OptionType.INTEGER, "perk", "Perk to upgrade", true).addChoices(getChoices()))
                .addOption(OptionType.INTEGER, "count", "Times to upgrade", false);
    }

    private Command.Choice[] getChoices() {
        Collection<GuildPerk> perks = bot.getGuildPerkManager().getPerks();
        Command.Choice[] choices = new Command.Choice[perks.size()];

        for (GuildPerk perk : perks) {
            choices[perk.id] = new Command.Choice(perk.name.toLowerCase(), perk.id);
        }

        return choices;
    }
}
