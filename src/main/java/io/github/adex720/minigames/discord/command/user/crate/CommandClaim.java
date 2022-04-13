package io.github.adex720.minigames.discord.command.user.crate;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.HashMap;

public class CommandClaim extends Command {

    public CommandClaim(MinigamesBot bot) {
        super(bot, "claim", "Claims all of your ready kits.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        OffsetDateTime time = OffsetDateTime.now();
        User user = ci.author();
        long userId = user.getIdLong();
        Profile profile = ci.profile();

        int claimed = 0;
        int coins = 0;
        HashMap<Integer, Integer> crates = new HashMap<>(); // Crate HashMap for received crates
        for (KitCommand command : bot.getKitCooldownManager().getKits()) {
            if (command.isOnCooldown(userId, time)) continue;

            claimed++;
            Pair<Integer, Integer> rewards = command.addRewardAndCooldown(event, profile, time); // First is received coins and right crates

            if (rewards.first > 0) { // Opening one crate never gives both coins and crates
                coins += rewards.first;
            } else {
                if (!crates.containsKey(rewards.second))
                    crates.put(rewards.second, 1); // Add crate rarity to HashMap if it's not present
                else crates.put(rewards.second, crates.get(rewards.second) + 1);
            }
        }

        if (claimed == 0) {
            event.getHook().sendMessage("None of your kits are ready!").queue();
            return true;
        }

        StringBuilder description = new StringBuilder(); // Rewards are saved. Proceeding to building message

        if (coins > 0) {
            description.append("You earned ").append(coins).append(" coins!");

            if (!crates.isEmpty()) description.append('\n');
        }

        if (!crates.isEmpty()) {
            description.append("You earned ");
            boolean comma = false;
            int total = 0;
            for (int type = 0; type < CrateType.TYPES_AMOUNT; type++) { // Looping through each crate rarity.
                // EntrySet of the HashMap is not used as it is not on the correct order
                if (!crates.containsKey(type)) continue; // skipping rarity if no crates were received from it

                int count = crates.get(type);
                total += count;
                BoosterRarity boosterRarity = BoosterRarity.get(type);
                while (count > 0) {
                    if (comma) description.append(", ");
                    comma = true;
                    description.append(boosterRarity.name);

                    count--;
                }
            }
            description.append(" crate");
            if (total > 1) description.append('s');
            description.append("!");
        }

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("CLAIM")
                .addField("You opened " + claimed + " kit" + (claimed > 1 ? "s" : "") + "!", description.toString(), false)
                .setColor(Util.getColor(userId))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
        return true;
    }
}
