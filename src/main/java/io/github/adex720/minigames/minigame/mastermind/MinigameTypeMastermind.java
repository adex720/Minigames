package io.github.adex720.minigames.minigame.mastermind;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.discord.command.Subcommand;
import io.github.adex720.minigames.discord.command.minigame.CommandMastermindPlace;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.MinigameType;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author adex720
 */
public class MinigameTypeMastermind extends MinigameType<MinigameMastermind> {

    public static final String[] COLOR_NAMES = new String[]{"red", "purple", "blue", "cyan", "green", "yellow", "orange", "brown"};
    public static final boolean[] HAS_CUSTOM_EMOTE = new boolean[]{false, false, false, true, false, false, false, false};

    public final String[] COLOR_EMOTES;

    public MinigameTypeMastermind(MinigamesBot bot, MinigameTypeManager typeManager) {
        super(bot, typeManager, "mastermind", false, 1);

        // There are 9 default emotes for circles, but black and white are not used resulting in one custom one being needed
        COLOR_EMOTES = new String[8];

        for (int i = 0; i < 8; i++) {
            String name = COLOR_NAMES[i] + "_circle";
            if (HAS_CUSTOM_EMOTE[i]) COLOR_EMOTES[i] = bot.getEmote(name);
            else COLOR_EMOTES[i] = ":" + name + ":";
        }
    }

    @Override
    public @Nullable MinigameMastermind create(SlashCommandEvent event, CommandInfo ci) {
        return MinigameMastermind.start(event, ci);
    }

    @Override
    public @Nullable MinigameMastermind create(ButtonClickEvent event, CommandInfo ci) {
        return MinigameMastermind.start(event, ci);
    }

    @Override
    public MinigameMastermind fromJson(JsonObject json) {
        return MinigameMastermind.fromJson(json, bot);
    }

    @Override
    public Set<Subcommand> getSubcommands() {
        return Set.of(new CommandMastermindPlace(bot, typeManager));
    }
}
