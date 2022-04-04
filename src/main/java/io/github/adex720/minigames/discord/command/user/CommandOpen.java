package io.github.adex720.minigames.discord.command.user;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import io.github.adex720.minigames.gameplay.profile.crate.CrateList;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.Date;
import java.util.HashMap;

public class CommandOpen extends Command {

    public CommandOpen(MinigamesBot bot) {
        super(bot, "open", "Opens crates.", CommandCategory.USER);
        requiresProfile();
    }

    @Override
    public boolean execute(SlashCommandEvent event, CommandInfo ci) {
        OptionMapping type = event.getOption("type");
        OptionMapping amount = event.getOption("amount");

        if (type == null && amount == null) {
            event.getHook().sendMessage("Please input a type!").queue();
            return true;
        }

        int typeId = -1;

        int count = 1;
        if (amount != null) {
            String amountString = amount.getAsString();

            try {
                count = Integer.parseInt(amountString);

                if (type == null) {
                    event.getHook().sendMessage("You can't open a specific amount of crates while not having a rarity.").queue();
                    return true;
                }

                if (count <= 0) {
                    event.getHook().sendMessage("You need to open at least one crate.").queue();
                    return true;
                }

                int maxCount = ci.profile().amountOfCrates((int) type.getAsLong());
                if (count > maxCount) {
                    event.getHook().sendMessage("You only have " + maxCount + " crates.").queue();
                    return true;
                }
            } catch (NumberFormatException ignored) {
                if (!amountString.equalsIgnoreCase("a")
                        && !amountString.equalsIgnoreCase("all")
                        && !amountString.equalsIgnoreCase("max")) {

                    if (Util.isUserNormal(amountString)) {
                        event.getHook().sendMessage(amountString + " is not a number or 'all'!").queue();
                    } else {
                        event.getHook().sendMessage("That is not a number!").queue();
                    }
                    return true;
                }

                if (type != null) {
                    typeId = (int) type.getAsLong();
                    count = ci.profile().amountOfCrates(typeId);

                    if (count == 0) {
                        event.getHook().sendMessage("You don't have any " + CrateType.get(typeId).name + " crates").queue();
                        return true;
                    }
                } else {
                    count = ci.profile().amountOfCrates();

                    if (count == 0) {
                        event.getHook().sendMessage("You don't have any crates").queue();
                        return true;
                    }
                }
            }
        }

        User user = ci.author();
        Profile profile = ci.profile();

        String description;
        if (count == 1) {
            if (type == null) description = profile.openCrate(profile.getFirstCrateRarityOnInventory());
            else description = profile.openCrate(typeId);
        } else if (type == null) {
            CrateList crateList = profile.getCrateList();

            int coins = 0;
            HashMap<Integer, Integer> boosters = new HashMap<>();

            int[] crateAmounts = crateList.values();
            for (int i = 0; i < CrateType.TYPES_AMOUNT; i++) {

                int rarityAmount = crateAmounts[i];
                if (rarityAmount == 0) continue;

                CrateType crateType = CrateType.get(i);
                while (rarityAmount > 0) {
                    int returned = crateType.applyRewardsAndReturnBoosterTypeOrCoinsNegative(bot, profile);

                    if (returned > 0) {
                        if (!boosters.containsKey(i)) boosters.put(i, 1);
                        else boosters.put(i, boosters.get(i) + 1);
                    } else {
                        coins -= returned;
                    }

                    rarityAmount--;
                }
            }

            StringBuilder stringBuilder = new StringBuilder();

            boolean newLine = false;
            if (coins > 0) {
                stringBuilder.append("- ").append(coins).append(" coins");
                newLine = true;
            }

            for (int i = 0; i < BoosterRarity.RARITIES_AMOUNT; i++) {
                if (!boosters.containsKey(i)) continue;

                if (newLine) stringBuilder.append('\n');
                newLine = true;

                BoosterRarity boosterRarity = BoosterRarity.get(i);
                stringBuilder.append("- ").append(boosters.get(i)).append("x ").append(boosterRarity.getEmoteName(bot))
                        .append(' ').append(boosterRarity.name);
            }

            description = stringBuilder.toString();
        } else {

            CrateType crateType = CrateType.get(typeId);

            if (!crateType.canContainBoosters) {
                profile.addCoins(count * crateType.coins, true);
                description = "- " + count * crateType.coins + " coins";
            } else if (!crateType.canContainCoins) {
                profile.addBoosters(typeId, count);
                description = "- " + count + "x " + crateType.boosterRarity.getEmoteName(bot) + " "
                        + crateType.boosterRarity.name + " boosters";
            } else {
                int coins = 0;
                int boosters = 0;
                for (int left = count; left > 0; left--) {
                    int returned = crateType.applyRewardsAndReturnBoosterTypeOrCoinsNegative(bot, profile);

                    if (returned > 0) {
                        boosters++;
                    } else {
                        coins -= returned;
                    }
                }

                if (coins > 0 && boosters > 0) {
                    description = "- " + coins + " coins\n- " + boosters + "x " + crateType.boosterRarity.getEmoteName(bot) + " "
                            + crateType.boosterRarity.name + " boosters";
                } else if (coins > 0) {
                    description = "- " + coins + " coins";
                } else {
                    description = "- " + boosters + "x " + crateType.boosterRarity.getEmoteName(bot) + " "
                            + crateType.boosterRarity.name + " boosters";
                }
            }

        }

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("OPEN CRATES")
                .addField("Opened " + count + " crates", description, false)
                .setColor(Util.getColor(user.getIdLong()))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
        return true;
    }

    @Override
    protected CommandData createCommandData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, "type", "Type of crate", false);

        for (int id = 0; id < CrateType.TYPES_AMOUNT; id++) {
            optionData.addChoice(CrateType.get(id).name, id);
        }

        return super.createCommandData()
                .addOptions(optionData)
                .addOption(OptionType.STRING, "amount", "Amount of crates to open", false);
    }

}
