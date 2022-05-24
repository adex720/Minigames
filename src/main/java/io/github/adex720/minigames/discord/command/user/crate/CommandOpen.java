package io.github.adex720.minigames.discord.command.user.crate;

import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.Command;
import io.github.adex720.minigames.discord.command.CommandCategory;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.gameplay.profile.booster.BoosterRarity;
import io.github.adex720.minigames.gameplay.profile.crate.CrateType;
import io.github.adex720.minigames.util.Pair;
import io.github.adex720.minigames.util.replyable.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Date;
import java.util.HashMap;

/**
 * @author adex720
 */
public class CommandOpen extends Command {

    public CommandOpen(MinigamesBot bot) {
        super(bot, "open", "Opens crates.", CommandCategory.USER);
        requiresProfile();
    }

    public OptionMapping getType(SlashCommandInteractionEvent event) {
        return event.getOption("type");
    }

    public OptionMapping getAmount(SlashCommandInteractionEvent event) {
        return event.getOption("amount");
    }

    public int getTypeId(SlashCommandInteractionEvent event) {
        OptionMapping type = getType(event);
        if (type == null) return -1;
        return (int) type.getAsLong();
    }

    @Override
    public boolean execute(SlashCommandInteractionEvent event, CommandInfo ci) {
        OptionMapping type = getType(event);
        OptionMapping amount = getAmount(event);
        if (type == null && amount == null) {
            event.getHook().sendMessage("Please input a type!").queue();
            return true;
        }

        int typeId = getTypeId(event);
        int count = 1;
        if (amount != null) {
            count = getOpeningCount(event, ci);
            if (count < 0) return true;
        }

        User user = ci.author();
        Replyable replyable = Replyable.from(event);

        String description = getDescription(replyable, ci.profile(), type, typeId, count);
        if (description.isEmpty()) return true; // Can't open any crates, replies are handled in the method

        event.getHook().sendMessageEmbeds(new EmbedBuilder()
                .setTitle("OPEN CRATES")
                .addField("Opened " + count + " crates", description, false)
                .setColor(Util.getColor(user.getIdLong()))
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build()).queue();
        return true;
    }

    /**
     * @return Amount of crates to open regarding the given argument. Returns -1 on invalid count or circumstances.
     */
    public int getOpeningCount(SlashCommandInteractionEvent event, CommandInfo ci) {
        String amountString = getAmount(event).getAsString();
        OptionMapping type = getType(event);

        try {
            int count = Integer.parseInt(amountString);

            if (type == null) {
                event.getHook().sendMessage("You can't open a specific amount of crates while not having a rarity.").queue();
                return -1;
            }

            if (count <= 0) {
                event.getHook().sendMessage("You need to open at least one crate.").queue();
                return -1;
            }

            int maxCount = ci.profile().amountOfCrates((int) type.getAsLong());
            if (maxCount == 0) {
                event.getHook().sendMessage("You don't have crates. You can get them from claiming kits or playing minigames.").queue();
                return -1;
            }

            if (count > maxCount) {
                event.getHook().sendMessage("You only have " + maxCount + " crates.").queue();
                return -1;
            }

            return count;
        } catch (NumberFormatException ignored) {
            if (!amountString.equalsIgnoreCase("a") // Checking for 'all'
                    && !amountString.equalsIgnoreCase("all")
                    && !amountString.equalsIgnoreCase("max")) {

                if (Util.isUserNormal(amountString)) {
                    event.getHook().sendMessage(amountString + " is not a number or 'all'!").queue();
                } else {
                    event.getHook().sendMessage("That is not a number!").queue();
                }
                return -1;
            }

            if (type != null) {
                int typeId = (int) type.getAsLong();
                int count = ci.profile().amountOfCrates(typeId);

                if (count == 0) {
                    event.getHook().sendMessage("You don't have any " + CrateType.get(typeId).name + " crates").queue();
                    return -1;
                }

                return count;
            } else {
                int count = ci.profile().amountOfCrates();

                if (count == 0) {
                    event.getHook().sendMessage("You don't have any crates").queue();
                    return -1;
                }
                return count;
            }
        }
    }

    /**
     * Creates description for given circumstance
     */
    public String getDescription(Replyable replyable, Profile profile, OptionMapping type, int typeId, int count) {
        if (count == 1) {
            if (type == null) return profile.openCrate(replyable, profile.getFirstCrateRarityOnInventory());
            else return profile.openCrate(replyable, typeId);

        } else if (type == null) {
            return openAll(replyable, profile);
        } else {
            return openAllFromRarity(replyable, CrateType.get(typeId), profile, count, typeId);
        }
    }

    /**
     * Opens all crates the user has
     */
    public String openAll(Replyable replyable, Profile profile) {
        Pair<Integer, HashMap<Integer, Integer>> loot = getLoot(replyable, profile);

        return lootToString(loot.first, loot.second);
    }

    /**
     * @return The loot the given user gets from opening all crates. The structure is like: Pair<Coins, HashMap<BoosterTypeId, Amount>>
     */
    public Pair<Integer, HashMap<Integer, Integer>> getLoot(Replyable replyable, Profile profile) {
        int coins = 0;
        HashMap<Integer, Integer> boosters = new HashMap<>();

        int[] crateAmounts = profile.getCrateList().values();
        for (int i = 0; i < CrateType.TYPES_AMOUNT; i++) { // Looping each crate type

            int rarityAmount = crateAmounts[i];
            if (rarityAmount == 0) continue;

            CrateType crateType = CrateType.get(i);
            while (rarityAmount > 0) { // Looping each crate of the type
                Pair<Integer, Integer> rewards = crateType.applyRewardsAndReturnCounts(replyable, bot, profile);

                if (rewards.second > 0) {
                    if (!boosters.containsKey(i)) boosters.put(i, 1);
                    else boosters.put(i, boosters.get(i) + 1);
                } else {
                    coins += rewards.first;
                }

                rarityAmount--;
            }
        }

        return new Pair<>(coins, boosters);
    }

    /**
     * @return list of coins and boosters as String
     */
    public String lootToString(int coins, HashMap<Integer, Integer> boosters) {
        StringBuilder lootString = new StringBuilder();
        boolean newLine = false;
        if (coins > 0) {
            lootString.append("- ").append(coins).append(" coins");
            newLine = true;
        }

        for (int i = 0; i < BoosterRarity.RARITIES_AMOUNT; i++) {
            if (!boosters.containsKey(i)) continue;

            if (newLine) lootString.append('\n');
            newLine = true;

            BoosterRarity boosterRarity = BoosterRarity.get(i);
            lootString.append("- ").append(boosters.get(i)).append("x ").append(boosterRarity.getEmoteName(bot))
                    .append(' ').append(boosterRarity.name);
        }

        return lootString.toString();
    }

    /**
     * Applies rewards and returns them as String. If the crate can contain both coins and booster this method uses {@link CommandOpen#openAllFromRarityContainingCoinsAndBoosters(Replyable, CrateType, Profile, int)}
     */
    public String openAllFromRarity(Replyable replyable, CrateType crateType, Profile profile, int count, int typeId) {
        if (!crateType.canContainBoosters) {
            profile.addCoins(count * crateType.coins, true, replyable);
            return "- " + count * crateType.coins + " coins";
        }

        if (!crateType.canContainCoins) {
            profile.addBoosters(typeId, count);
            return "- " + count + "x " + crateType.boosterRarity.getEmoteName(bot) + " "
                    + crateType.boosterRarity.name + " boosters";
        }

        return openAllFromRarityContainingCoinsAndBoosters(replyable, crateType, profile, count);
    }

    /**
     * Applies rewards and returns them as String.
     * For optimization reason only call this when the crate can contain both cains and boosters.
     */
    public String openAllFromRarityContainingCoinsAndBoosters(Replyable replyable, CrateType crateType, Profile profile, int count) {
        Pair<Integer, Integer> loot = getCrateLootFromRarity(replyable, crateType, profile, count);
        int coins = loot.first;
        int boosters = loot.second;

        if (coins > 0 && boosters > 0) {
            return "- " + coins + " coins\n- " + boosters + "x " + crateType.boosterRarity.getEmoteName(bot) + " "
                    + crateType.boosterRarity.name + " boosters";
        } else if (coins > 0) {
            return "- " + coins + " coins";
        } else {
            return "- " + boosters + "x " + crateType.boosterRarity.getEmoteName(bot) + " "
                    + crateType.boosterRarity.name + " boosters";
        }
    }

    /**
     * @return A {@link Pair} containing the rewards of one crate opened from the {@param crateType}.
     * The left is coins and right is the id of the booster.
     * Since one crate only gives coins or boosters one of the values is always set to its default value.
     * For coins, it's 0 and for booster id -1.
     */
    public Pair<Integer, Integer> getCrateLootFromRarity(Replyable replyable, CrateType crateType, Profile profile, int count) {
        int coins = 0;
        int boosters = 0;
        for (int left = count; left > 0; left--) {
            Pair<Integer, Integer> rewards = crateType.applyRewardsAndReturnCounts(replyable, bot, profile);

            if (rewards.second > 0) {
                boosters++;
            } else {
                coins += rewards.first;
            }
        }

        profile.appendQuests(quest -> quest.crateOpened(replyable, crateType, profile));
        return new Pair<>(coins, boosters);
    }

    @Override
    protected SlashCommandData createCommandData() {
        OptionData optionData = new OptionData(OptionType.INTEGER, "type", "Type of crate", false);

        for (int id = 0; id < CrateType.TYPES_AMOUNT; id++) {
            optionData.addChoice(CrateType.get(id).name, id);
        }

        return super.createCommandData()
                .addOptions(optionData)
                .addOption(OptionType.STRING, "amount", "Amount of crates to open", false);
    }

}
