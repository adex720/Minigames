package io.github.adex720.minigames;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.discord.listener.*;
import io.github.adex720.minigames.gameplay.guild.GuildBossList;
import io.github.adex720.minigames.gameplay.manager.command.CommandManager;
import io.github.adex720.minigames.gameplay.manager.command.PageMovementManager;
import io.github.adex720.minigames.gameplay.manager.guild.GuildBossManager;
import io.github.adex720.minigames.gameplay.manager.guild.GuildManager;
import io.github.adex720.minigames.gameplay.manager.minigame.ReplayManager;
import io.github.adex720.minigames.gameplay.manager.data.BotDataManager;
import io.github.adex720.minigames.gameplay.manager.data.ResourceDataManager;
import io.github.adex720.minigames.gameplay.manager.file.FilePathManager;
import io.github.adex720.minigames.gameplay.manager.kit.KitCooldownManager;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameManager;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.gameplay.manager.party.PartyManager;
import io.github.adex720.minigames.gameplay.manager.profile.BadgeManager;
import io.github.adex720.minigames.gameplay.manager.profile.BanManager;
import io.github.adex720.minigames.gameplay.manager.profile.ProfileManager;
import io.github.adex720.minigames.gameplay.manager.quest.QuestManager;
import io.github.adex720.minigames.gameplay.manager.stat.StatManager;
import io.github.adex720.minigames.gameplay.manager.timer.TimerManager;
import io.github.adex720.minigames.gameplay.manager.word.WordManager;
import io.github.adex720.minigames.gameplay.profile.quest.QuestList;
import io.github.adex720.minigames.gameplay.profile.settings.SettingsList;
import io.github.adex720.minigames.minigame.duel.connect4.Connect4ButtonManager;
import io.github.adex720.minigames.minigame.duel.tictactoe.TicTacToeButtonManager;
import io.github.adex720.minigames.minigame.gamble.blackjack.BlackjackButtonManager;
import io.github.adex720.minigames.minigame.party.memo.ImageBank;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Util;
import io.github.adex720.minigames.util.network.HttpsRequester;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.CheckReturnValue;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Random;

/**
 * Contains all event listeners, managers and other objects the bot has.
 * Also contains the JDA instance, as well as the main method.
 *
 * @author adex720
 */
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
    private final PageMovementManager pageMovementManager;
    private final BlackjackButtonManager blackjackButtonManager;
    private final TicTacToeButtonManager ticTacToeButtonManager;
    private final Connect4ButtonManager connect4ButtonManager;


    private final GuildJoinListener guildJoinListener;
    private final SelfMentionListener selfMentionListener;
    private final CountingListener countingListener;

    private final BadgeManager badgeManager;
    private final StatManager statManager;

    private final ImageBank memoImageBank;

    private final GuildBossManager guildBossManager;
    private final GuildBossList guildBossList;

    private final BanManager banManager;
    private final ProfileManager profileManager;

    private final PartyManager partyManager;
    private final GuildManager guildManager;

    private final BotDataManager saveDataManager;
    private final ResourceDataManager resourceDataManager;

    private final MinigameTypeManager minigameTypeManager;
    private final MinigameManager minigameManager;

    private final FilePathManager filePathManager;
    private final WordManager wordManager;

    private final QuestManager questManager;
    private final QuestList questList;

    private final TimerManager timerManager;

    private final KitCooldownManager kitCooldownManager;

    private final SettingsList settingsList;

    private final HttpsRequester httpsRequester;

    private int linesOfCodeTotal;
    private int linesOfCodeJava;
    private int linesOfCodeJson;

    public MinigamesBot(String token, JsonObject databaseConfig, long developerId) throws LoginException, InterruptedException, FileNotFoundException {
        long startTime = System.currentTimeMillis();
        logger = LoggerFactory.getLogger(MinigamesBot.class);

        random = new Random();

        saveDataManager = new BotDataManager(this, databaseConfig);
        resourceDataManager = new ResourceDataManager(this);

        settingsList = new SettingsList();
        settingsList.init(this);

        emoteJson = getResourceJson("emotes").getAsJsonObject();

        commandManager = new CommandManager(this);
        commandListener = new CommandListener(this, commandManager);

        devCommandListener = new DevCommandListener(this, developerId, "-");


        buttonListener = new ButtonListener(this);

        replayManager = new ReplayManager(this);
        pageMovementManager = new PageMovementManager(this);
        blackjackButtonManager = new BlackjackButtonManager(this);
        ticTacToeButtonManager = new TicTacToeButtonManager(this);
        connect4ButtonManager = new Connect4ButtonManager(this);


        guildJoinListener = new GuildJoinListener(this);
        selfMentionListener = new SelfMentionListener(this);
        countingListener = new CountingListener(this);

        badgeManager = new BadgeManager(this);
        statManager = new StatManager(this);

        questManager = new QuestManager(this);
        questList = new QuestList(this);

        guildBossManager = new GuildBossManager(this);
        guildBossList = new GuildBossList(this);

        banManager = new BanManager(this);
        profileManager = new ProfileManager(this);
        statManager.initLeaderboards();

        partyManager = new PartyManager(this);
        guildManager = new GuildManager(this);

        filePathManager = new FilePathManager(this);
        wordManager = new WordManager(this);

        memoImageBank = new ImageBank(this);

        minigameTypeManager = new MinigameTypeManager(this);
        minigameManager = new MinigameManager(this);

        timerManager = new TimerManager(this);

        kitCooldownManager = new KitCooldownManager(this);


        commandManager.initCommands(this);

        jda = JDABuilder.createDefault(token)
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.watching("/help"))
                .addEventListeners(commandListener, buttonListener, devCommandListener, guildJoinListener, selfMentionListener, countingListener)
                .build()
                .awaitReady();
        long botOnlineTime = System.currentTimeMillis();

        commandManager.registerCommands(jda);

        selfMentionListener.init();

        commandManager.commandUptime.setStarted(startTime);
        commandManager.commandUptime.botOnline(botOnlineTime);

        startTimers();

        httpsRequester = new HttpsRequester();

        calculateLinesOfCode();
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

    private void startTimers() {
        addTimerTask(this::clearInactive, 1000 * 60 * 5, true); // Delete inactive parties and minigames
        addTimerTask(resourceDataManager::clearCache, 1000 * 60 * 60 * 6, true); // Clear cached resource json files

        addTimerTask(questManager::unloadQuests, Util.MILLISECONDS_IN_DAY, Util.getMillisecondsUntilUtcMidnight()); // Unload all quests at UTC midnight
        addTimerTask(guildManager::onNewWeek, Util.MILLISECONDS_IN_WEEK, Util.getMillisecondsUntilUtcNewWeek()); // Reset guild weekly progress at new UTC week

        addTimerTask(this::save, 1000 * 60 * 5, true); // save data
    }

    @CheckReturnValue
    public JDA getJda() {
        return jda;
    }

    @CheckReturnValue
    public Logger getLogger() {
        return logger;
    }

    @CheckReturnValue
    public CommandManager getCommandManager() {
        return commandManager;
    }

    @CheckReturnValue
    public CommandListener getCommandListener() {
        return commandListener;
    }

    @CheckReturnValue
    public DevCommandListener getDevCommandListener() {
        return devCommandListener;
    }

    @CheckReturnValue
    public ButtonListener getButtonListener() {
        return buttonListener;
    }

    @CheckReturnValue
    public ReplayManager getReplayManager() {
        return replayManager;
    }

    @CheckReturnValue
    public PageMovementManager getPageMovementManager() {
        return pageMovementManager;
    }

    @CheckReturnValue
    public BlackjackButtonManager getBlackjackButtonManager() {
        return blackjackButtonManager;
    }

    @CheckReturnValue
    public TicTacToeButtonManager getTicTacToeButtonManager() {
        return ticTacToeButtonManager;
    }

    @CheckReturnValue
    public Connect4ButtonManager getConnect4ButtonManager() {
        return connect4ButtonManager;
    }


    @CheckReturnValue
    public GuildJoinListener getGuildJoinListener() {
        return guildJoinListener;
    }

    @CheckReturnValue
    public SelfMentionListener getSelfMentionListener() {
        return selfMentionListener;
    }

    @CheckReturnValue
    public CountingListener getCountingListener() {
        return countingListener;
    }

    @CheckReturnValue
    public BanManager getBanManager() {
        return banManager;
    }

    @CheckReturnValue
    public ProfileManager getProfileManager() {
        return profileManager;
    }

    @CheckReturnValue
    public MinigameTypeManager getMinigameTypeManager() {
        return minigameTypeManager;
    }

    @CheckReturnValue
    public PartyManager getPartyManager() {
        return partyManager;
    }

    public GuildManager getGuildManager() {
        return guildManager;
    }

    @CheckReturnValue
    public MinigameManager getMinigameManager() {
        return minigameManager;
    }

    @CheckReturnValue
    public FilePathManager getFilePathManager() {
        return filePathManager;
    }

    @CheckReturnValue
    public WordManager getWordManager() {
        return wordManager;
    }

    @CheckReturnValue
    public BadgeManager getBadgeManager() {
        return badgeManager;
    }

    @CheckReturnValue
    public StatManager getStatManager() {
        return statManager;
    }

    @CheckReturnValue
    public QuestManager getQuestManager() {
        return questManager;
    }

    @CheckReturnValue
    public QuestList getQuestList() {
        return questList;
    }

    public GuildBossManager getGuildBossManager() {
        return guildBossManager;
    }

    public GuildBossList getGuildBossList() {
        return guildBossList;
    }

    @CheckReturnValue
    public Random getRandom() {
        return random;
    }

    @CheckReturnValue
    public KitCooldownManager getKitCooldownManager() {
        return kitCooldownManager;
    }

    @CheckReturnValue
    public SettingsList getSettingsList() {
        return settingsList;
    }

    @CheckReturnValue
    public long getEmoteId(String name) {
        return JsonHelper.getLong(emoteJson, name, 1L);
    }

    @CheckReturnValue
    public String getEmote(String name) {
        return "<:" + name + ":" + getEmoteId(name) + ">";
    }

    @CheckReturnValue
    public int getLinesOfCodeTotal() {
        return linesOfCodeTotal;
    }

    @CheckReturnValue
    public int getLinesOfCodeJava() {
        return linesOfCodeJava;
    }

    @CheckReturnValue
    public int getLinesOfCodeJson() {
        return linesOfCodeJson;
    }

    @CheckReturnValue
    public ImageBank getMemoImageBank() {
        return memoImageBank;
    }

    public void addTimerTask(TimerManager.Task task, int delay, boolean repeat) {
        timerManager.add(task, delay, repeat);
    }

    public void addTimerTask(TimerManager.Task task, int delay, int firstDelay) {
        timerManager.add(task, delay, firstDelay);
    }


    public void calculateLinesOfCode() {
        String request = "https://api.codetabs.com/v1/loc?github=adex720/Minigames&ignored=words";

        JsonArray jsonArray;
        try {
            jsonArray = httpsRequester.requestJson(request).getAsJsonArray();
        } catch (Exception e) {
            logger.error("Failed to get amount of lines of code!" + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            return;
        }

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            switch (jsonObject.get("language").getAsString()) {
                case "Java" -> linesOfCodeJava = jsonObject.get("lines").getAsInt();
                case "JSON" -> linesOfCodeJson = jsonObject.get("lines").getAsInt();
                case "Total" -> linesOfCodeTotal = jsonObject.get("lines").getAsInt();
            }
        }

        logger.info("Loaded amount of lines of code!");
    }


    @CheckReturnValue
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

    @CheckReturnValue
    public JsonElement getResourceJson(String name) {
        return resourceDataManager.loadJson(name);
    }

    public void saveJson(JsonElement json, String name) {
        if (!saveDataManager.saveJson(json, name)) {
            logger.error("Failed to save json file {}", name);
        }
    }

    @CheckReturnValue
    public JsonElement loadJson(String name) {
        return saveDataManager.loadJson(name);
    }

    public void save() {
        long start = System.currentTimeMillis();

        saveJson(profileManager.asJson(), "profiles");
        saveJson(partyManager.asJson(), "parties");
        saveJson(guildManager.asJson(), "guilds");
        saveJson(minigameManager.asJson(), "minigames");

        long end = System.currentTimeMillis();
        logger.info("Saved all data in {}ms", end - start);
    }

    public void reload() {
        long start = System.currentTimeMillis();

        profileManager.load( saveDataManager.loadJson("profiles").getAsJsonArray());
        partyManager.load( saveDataManager.loadJson("parties").getAsJsonArray());
        guildManager.load( saveDataManager.loadJson("guilds").getAsJsonArray());
        minigameManager.load( saveDataManager.loadJson("minigames").getAsJsonArray());

        long end = System.currentTimeMillis();
        logger.info("Reloaded all data in {}ms", end - start);
    }

    public void stop() {
        jda.shutdown();
        timerManager.stop();
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

    TODO: buttons to add

    TODO: merge the two start- and create-methods

    TODO: save saving time on

    TODO: guilds:
     - wars (more minigames completed)
     - leaderboard

    TODO: trivia (party)
     (https://opentdb.com/api_config.php)

    TODO: (After a long time)
     - vote (after verification)
     - global boosters (after votes)
     - sharding (at 2000 guilds)
     - ensure not overcoming ratelimit

*/