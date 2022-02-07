package io.github.adex720.minigames;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.discord.listener.CommandListener;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import io.github.adex720.minigames.gameplay.manager.party.PartyManager;
import io.github.adex720.minigames.gameplay.manager.profile.ProfileManager;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class MinigamesBot {

    private final JDA jda;

    private final Logger logger;


    private final CommandManager commandManager;
    private final CommandListener commandListener;

    private final ProfileManager profileManager;

    private final PartyManager partyManager;

    public MinigamesBot(String token) throws LoginException, InterruptedException {
        logger = LoggerFactory.getLogger(MinigamesBot.class);


        commandManager = new CommandManager(this);
        commandListener = new CommandListener(this, commandManager);
        commandManager.initCommands(this);

        profileManager = new ProfileManager(this);

        partyManager = new PartyManager(this);

        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("/help"))
                .addEventListeners(commandListener)
                .build()
                .awaitReady();

        commandManager.registerCommands(jda);
    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        JsonObject configJson = getConfigJson();

        String token = JsonHelper.getStringOrThrow(configJson, "token", "Missing entry on config json: token");

        MinigamesBot minigamesBot = new MinigamesBot(token);
    }

    public JDA getJda() {
        return jda;
    }

    public Logger getLogger() {
        return logger;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public CommandListener getCommandListener() {
        return commandListener;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    private static JsonObject getConfigJson() {
        String filePath = "config.json";

        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Config json file not found!");
            System.exit(1);
            return new JsonObject();
        }

        JsonObject json;
        try {
            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            json = new Gson().fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            System.out.println("Invalid config json!");
            System.exit(1);
            return new JsonObject();
        }

        return json;
    }
}


/*
   TODO: minigames to add
    - hangman
    - unscramble
    - wordle
    - higher-lower
    - counting
    - counting variations (hex, binary, letters)
    - tic-tac-toe
    - connect 4

  TODO: commands to add
    - help
    - leaderboard
    - quests
    - profile
    - kits (daily, hourly, supporter)

  TODO: trivia
   (https://opentdb.com/api_config.php)


*/