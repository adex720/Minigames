package io.github.adex720.minigames;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.discord.listener.ButtonListener;
import io.github.adex720.minigames.discord.listener.CommandListener;
import io.github.adex720.minigames.discord.listener.DevCommandListener;
import io.github.adex720.minigames.discord.listener.GuildJoinListener;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import io.github.adex720.minigames.gameplay.manager.command.ReplayManager;
import io.github.adex720.minigames.gameplay.manager.data.BotDataManager;
import io.github.adex720.minigames.gameplay.manager.data.ResourceDataManager;
import io.github.adex720.minigames.gameplay.manager.file.FilePathManager;
import io.github.adex720.minigames.gameplay.manager.kit.KitCooldownManager;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameManager;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.gameplay.manager.party.PartyManager;
import io.github.adex720.minigames.gameplay.manager.profile.BadgeManager;
import io.github.adex720.minigames.gameplay.manager.profile.ProfileManager;
import io.github.adex720.minigames.gameplay.manager.quest.QuestManager;
import io.github.adex720.minigames.gameplay.manager.stat.LeaderboardManager;
import io.github.adex720.minigames.gameplay.manager.stat.StatManager;
import io.github.adex720.minigames.gameplay.manager.timer.TimerManager;
import io.github.adex720.minigames.gameplay.manager.word.WordManager;
import io.github.adex720.minigames.gameplay.profile.quest.QuestList;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

public class MinigamesBot {

    private final JDA jda;

    private final Logger logger;

    private final Random random;

    private final JsonObject emoteJson;


    private final CommandManager commandManager;
    private final CommandListener commandListener;

    private final DevCommandListener devCommandListener;

    private final ButtonListener buttonListener;
    private final ReplayManager replayManager;

    private final GuildJoinListener guildJoinListener;

    private final BadgeManager badgeManager;
    private final StatManager statManager;

    private final ProfileManager profileManager;

    private final PartyManager partyManager;

    private final BotDataManager saveDataManager;
    private final ResourceDataManager resourceDataManager;

    private final MinigameTypeManager minigameTypeManager;
    private final MinigameManager minigameManager;

    private final FilePathManager filePathManager;
    private final WordManager wordManager;

    private final QuestManager questManager;
    private final QuestList questList;

    private final TimerManager timerManager;
    private final LeaderboardManager leaderboardManager;

    private final KitCooldownManager kitCooldownManager;

    public MinigamesBot(String token, JsonObject databaseConfig, long developerId) throws LoginException, InterruptedException, FileNotFoundException {
        long startTime = System.currentTimeMillis();
        logger = LoggerFactory.getLogger(MinigamesBot.class);

        random = new Random();

        saveDataManager = new BotDataManager(this, databaseConfig);
        resourceDataManager = new ResourceDataManager(this);

        commandManager = new CommandManager(this);
        commandListener = new CommandListener(this, commandManager);

        devCommandListener = new DevCommandListener(this, developerId, "-");

        buttonListener = new ButtonListener(this);
        replayManager = new ReplayManager(this);

        guildJoinListener = new GuildJoinListener(this);

        badgeManager = new BadgeManager(this);
        statManager = new StatManager(this);

        questManager = new QuestManager(this);
        questList = new QuestList(this);

        profileManager = new ProfileManager(this);

        partyManager = new PartyManager(this);

        minigameTypeManager = new MinigameTypeManager(this);
        minigameManager = new MinigameManager(this);

        filePathManager = new FilePathManager(this);
        wordManager = new WordManager(this);

        timerManager = new TimerManager(this);
        leaderboardManager = new LeaderboardManager(this);

        kitCooldownManager = new KitCooldownManager(this);

        commandManager.initCommands(this);

        emoteJson = getResourceJson("emotes").getAsJsonObject();

        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("/help"))
                .addEventListeners(commandListener, buttonListener, devCommandListener, guildJoinListener)
                .build()
                .awaitReady();
        long botOnlineTime = System.currentTimeMillis();

        commandManager.registerCommands(jda);

        commandManager.commandUptime.setStarted(startTime);
        commandManager.commandUptime.botOnline(botOnlineTime);

        leaderboardManager.start();

        addTimerTask(this::save, 1000 * 60 * 5, true);
        addTimerTask(this::clearInactive, 1000 * 60 * 5, true);
        addTimerTask(resourceDataManager::clearCache, 1000 * 60 * 60 * 6, true);
    }

    public static void main(String[] args) {
        JsonObject configJson = getConfigJson();

        String token = JsonHelper.getStringOrThrow(configJson, "token", "Missing entry on config json: token");
        JsonObject databaseConfig = JsonHelper.getJsonObjectOrThrow(configJson, "database", "Missing database information on config json");

        long developerId = JsonHelper.getLong(configJson, "developer");

        MinigamesBot minigamesBot = null;
        try {
            minigamesBot = new MinigamesBot(token, databaseConfig, developerId);
        } catch (LoginException e) {
            System.out.println("Invalid token for Discord bot!");
            System.exit(-1);
        } catch (InterruptedException e) {
            System.out.println("Failed to wait for bot to finish!");
            System.exit(-1);
        } catch (Exception e) {
            if (minigamesBot != null) {
                minigamesBot.onException(e);
            } else {
                System.out.println("Unknown error happened while setting up: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }
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

    public DevCommandListener getDevCommandListener() {
        return devCommandListener;
    }

    public ButtonListener getButtonListener() {
        return buttonListener;
    }

    public ReplayManager getReplayManager() {
        return replayManager;
    }

    public GuildJoinListener getGuildJoinListener() {
        return guildJoinListener;
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public MinigameTypeManager getMinigameTypeManager() {
        return minigameTypeManager;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public MinigameManager getMinigameManager() {
        return minigameManager;
    }

    public FilePathManager getFilePathManager() {
        return filePathManager;
    }

    public WordManager getWordManager() {
        return wordManager;
    }

    public BadgeManager getBadgeManager() {
        return badgeManager;
    }

    public StatManager getStatManager() {
        return statManager;
    }

    public QuestManager getQuestManager() {
        return questManager;
    }

    public QuestList getQuestList() {
        return questList;
    }

    public Random getRandom() {
        return random;
    }

    public KitCooldownManager getKitCooldownManager() {
        return kitCooldownManager;
    }

    public long getEmoteId(String name) {
        return JsonHelper.getLong(emoteJson, name, 1L);
    }

    public String getEmote(String name){
        return "<:" + name + ":" + getEmoteId(name) + ">";
    }

    public void addTimerTask(TimerManager.Task task, int delay, boolean repeat) {
        timerManager.add(task, delay, repeat);
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

    public JsonElement getResourceJson(String name) {
        return resourceDataManager.loadJson(name);
    }

    public void saveJson(JsonElement json, String name) {
        if (!saveDataManager.saveJson(json, name)) {
            logger.error("Failed to save json file {}", name);
        }
    }

    public JsonElement loadJson(String name) {
        return saveDataManager.loadJson(name);
    }

    public void save() {
        long start = System.currentTimeMillis();

        saveJson(profileManager.asJson(), "profiles");
        saveJson(partyManager.asJson(), "parties");
        saveJson(minigameManager.asJson(), "minigames");

        long end = System.currentTimeMillis();
        logger.info("Saved all data in {}ms", end - start);
    }

    public void reload() {
        long start = System.currentTimeMillis();

        profileManager.load((JsonArray) saveDataManager.loadJson("profiles"));
        partyManager.load((JsonArray) saveDataManager.loadJson("parties"));
        minigameManager.load((JsonArray) saveDataManager.loadJson("minigames"));

        long end = System.currentTimeMillis();
        logger.info("Reloaded all data in {}ms", end - start);
    }

    public void stop() {
        jda.shutdown();
        timerManager.stop();
        leaderboardManager.interrupt();
    }

    public void clearInactive() {
        minigameManager.clearInactiveMinigames();
        partyManager.clearInactiveParties();
    }

    private void onException(Exception exception) {
        logger.error("Exception: {} Message: {} Stack-trace: {}", exception.getClass().getPackageName() + "." + exception.getClass().getName(), exception.getMessage(), exception.getStackTrace());
    }
}

/*
    TODO: minigames to add
     - wordle
     - counting
     - counting variations (hex, binary, letters)
     - connect 4

   TODO: rewards for finished minigames

   TODO: guilds

   TODO: global boosters

   TODO: trivia
    (https://opentdb.com/api_config.php)

   TODO: tips for minigames

   TODO: (After a long time)
    - vote (after verification)
    - sharding (at 2000 guilds)

*/