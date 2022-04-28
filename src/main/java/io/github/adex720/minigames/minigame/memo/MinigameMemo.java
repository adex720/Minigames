package io.github.adex720.minigames.minigame.memo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.minigame.party.PartyCompetitiveMinigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

/**
 * @author adex720
 */
public class MinigameMemo extends PartyCompetitiveMinigame {

    public static final int DIFFERENT_CARDS = 0x3F;

    public final int width;
    public final int height;

    private final int[][] cards;
    private final int[] scores;

    private final long[] players;
    private int currentPlayerIndex;

    private boolean onBlock; // Block commands when cards are shown

    public MinigameMemo(MinigamesBot bot, long id, long lastActive, int[][] cards, long[] players, int currentPlayerIndex, int[] scores) {
        super(bot, bot.getMinigameTypeManager().MEMO, id, lastActive);

        this.cards = cards;
        this.width = cards.length;
        this.height = cards[0].length;

        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;

        this.scores = scores;

        onBlock = false;
    }

    public MinigameMemo(MinigamesBot bot, Party party, long lastActive) {
        super(bot, bot.getMinigameTypeManager().MEMO, party.getId(), lastActive);
        Random random = bot.getRandom();

        int playersAmount = party.size(); // get player count
        players = new long[playersAmount]; // init array
        ArrayList<Long> playersArrayList = new ArrayList<>(party.getMembersWithOwner()); // create arraylist with all players
        Collections.shuffle(playersArrayList, random); // shuffle players
        for (int i = 0; i < playersAmount; i++) { // add player ids to array
            players[i] = playersArrayList.get(i);
        }
        currentPlayerIndex = random.nextInt(playersAmount); // randomize starter

        this.width = getWidth(playersAmount);
        this.height = getHeight(playersAmount);
        this.cards = shuffleCards(random);

        this.scores = new int[playersAmount];
        Arrays.fill(this.scores, 0);

        onBlock = false;
    }

    public MinigameMemo(CommandInfo commandInfo) {
        this(commandInfo.bot(), commandInfo.party(), System.currentTimeMillis());
        shuffleCards(commandInfo.bot().getRandom());
    }

    public int getWidth(int players) {
        return switch (players) {
            case 3 /*     */ -> 5;
            case 2, 4, 6/**/ -> 7;
            case 5, 7, 8, 10 -> 8;
            case 9 /*     */ -> 10;

            default -> 0;
        };
    }

    public int getHeight(int players) {
        return switch (players) {
            case 2 /* */ -> 2;
            case 3, 4, 5 -> 4;
            case 6, 7, 9 -> 6;
            case 8 /* */ -> 7;
            case 10 /**/ -> 8;

            default -> 0;
        };
    }

    /**
     * Must be called after {@link MinigameMemo#width} and {@link MinigameMemo#height} are initialized.
     *
     * @return a 2d-array containing the cards.
     */
    public int[][] shuffleCards(Random random) {
        int[][] cards = new int[width][height];

        int pairs = width * height / 2; // Amount of cards is always a multiply of 2.

        // Randomize which cards are used on the game.
        // No duplicate images must be chosen.
        int[] cardIds = getDistinctRandomNumbers(pairs, DIFFERENT_CARDS, random);

        // Shuffle cards
        ArrayList<Integer> shuffled = new ArrayList<>();
        for (int i = 0; i < pairs; i++) {
            shuffled.add(i); // Add each card id twice
            shuffled.add(i);
        }
        Collections.shuffle(shuffled);

        // fill board with random cards
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int cardId = shuffled.get(index);
                cards[x][y] = cardIds[cardId];

                index++;
            }
        }

        return cards;
    }

    /**
     * Generates an array of min(count, maxValue) distinct random ints
     * from [0, maxValue - 1] range.
     *
     * @param count    The number of elements to be generated.
     * @param maxValue The upper bound of the range(exclusively).
     * @author rtruszk (https://stackoverflow.com/a/28152158/15093711)
     */
    public static int[] getDistinctRandomNumbers(int count, int maxValue, Random random) {
        Set<Integer> was = new HashSet<>();
        for (int i = Math.max(0, maxValue - count); i < maxValue; i++) {
            int curr = i == 0 ? 0 : random.nextInt(i);
            if (was.contains(curr))
                curr = i;
            was.add(curr);
        }
        return setToArray(was);
    }

    private static int[] setToArray(Set<Integer> aSet) {
        int[] result = new int[aSet.size()];
        int index = 0;
        for (int number : aSet) {
            result[index] = number;
            index++;
        }
        return result;
    }

    public static MinigameMemo start(SlashCommandEvent event, CommandInfo commandInfo) {
        MinigameMemo minigame = new MinigameMemo(commandInfo);

        event.getHook().sendMessage("You started a new game of memo.").queue();

        return minigame;
    }

    public static MinigameMemo start(ButtonClickEvent event, CommandInfo commandInfo) {
        MinigameMemo minigame = new MinigameMemo(commandInfo);

        event.getHook().sendMessage("You started a new game of memo.").queue();

        return minigame;
    }

    public static MinigameMemo fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        long active = JsonHelper.getLong(json, "active");

        JsonArray cardsJson = JsonHelper.getJsonArray(json, "cards");
        int rows = cardsJson.size();
        int[][] cards = new int[rows][cardsJson.get(0).getAsJsonArray().size()];
        for (int i = 0; i < rows; i++) {
            JsonArray rowJson = cardsJson.get(i).getAsJsonArray();
            cards[i] = JsonHelper.jsonArrayToIntArray(rowJson);
        }

        JsonArray playersJson = JsonHelper.getJsonArray(json, "players");
        long[] players = JsonHelper.jsonArrayToLongArray(playersJson);
        int currentPlayerIndex = JsonHelper.getInt(json, "turn");

        JsonArray scoresJson = JsonHelper.getJsonArray(json, "scores");
        int[] scores = JsonHelper.jsonArrayToIntArray(scoresJson);

        return new MinigameMemo(bot, id, active, cards, players, currentPlayerIndex, scores);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "memo");

        json.addProperty("id", id);
        json.addProperty("party", isParty);
        json.addProperty("active", lastActive);

        JsonArray cardsJson = new JsonArray();
        for (int[] row : cards) {
            cardsJson.add(JsonHelper.arrayToJsonArray(row));
        }
        json.add("cards", cardsJson);

        json.add("scores", JsonHelper.arrayToJsonArray(scores));

        json.add("players", JsonHelper.arrayToJsonArray(players));
        json.addProperty("turn", currentPlayerIndex);


        return json;
    }

    public void turn(SlashCommandEvent event, CommandInfo ci) throws IOException {
        int x1 = (int) event.getOption("column1").getAsLong();
        int y1 = (int) event.getOption("row1").getAsLong();
        int x2 = (int) event.getOption("column2").getAsLong();
        int y2 = (int) event.getOption("row2").getAsLong();

        long userId = players[currentPlayerIndex];
        if (ci.authorId() != userId) {
            event.getHook().sendMessage("It is not your turn!").queue();
            return;
        }

        if (x1 >= width || x1 < 0 || y1 >= height || y1 < 0) {
            event.getHook().sendMessage("The first card is outside the board!").queue();
            return;
        }

        if (x2 >= width || x2 < 0 || y2 >= height || y2 < 0) {
            event.getHook().sendMessage("The second card is outside the board!").queue();
            return;
        }

        int id1 = cards[x1][y1];
        if (id1 == -1) {
            event.getHook().sendMessage("The first tile is empty!").queue();
            return;
        }

        int id2 = cards[x2][y2];
        if (id2 == -1) {
            event.getHook().sendMessage("The second tile is empty!").queue();
            return;
        }

        BufferedImage image = getImage(x1, y1, x2, y2);
        File data = new File("memo1.png");
        ImageIO.write(image, "png", data);
        if (id1 == id2) {
            scores[currentPlayerIndex]++;
            cards[x1][y1] = -1;
            cards[x2][y2] = -1;
            if (isEmpty()) {
                event.getHook().sendMessage("All pairs were found! The scores are:\n" + getScores()).queue();
                finish(Replyable.from(event), ci, true);
                return;
            }
            event.getHook().sendMessage("You found a pair and got another turn! The scores are:\n" + getScores())
                    .addFile(data).queue();

        } else {
            currentPlayerIndex++;
            if (currentPlayerIndex == players.length) currentPlayerIndex = 0;
            event.getHook().sendMessage("You didn't find a pair.").addFile(data)
                    .delay(Duration.ofSeconds(5))
                    .flatMap(Message::delete).queue(); // Delete image after 5 seconds.

            BufferedImage background = getBack();
            File backgroundData = new File("memo2.png");
            ImageIO.write(background, "png", backgroundData);
            event.getHook().sendMessage("<@" + players[currentPlayerIndex] + ">, It's soon your turn!")
                    .delay(Duration.ofSeconds(6))
                    .flatMap(m->m.editMessage("<@" + players[currentPlayerIndex] + ">, It's now your turn!")
                            .addFile(backgroundData))
                            .queue();

        }
    }

    public BufferedImage getImage(int x1, int y1, int x2, int y2) {
        int imageWidth = width * 55 - 5;
        int imageHeight = height * 55 - 5;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();

        ImageBank imageBank = bot.getMemoImageBank();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cards[x][y] == -1) continue;

                int posX = x * 55;
                int posY = y * 55;
                if ((x == x1 && y == y1) || (x == x2 && y == y2)) {
                    imageBank.drawCard(g, posX, posY, cards[x][y]);
                    continue;
                }

                imageBank.drawBackground(g, posX, posY);
            }
        }

        return image;
    }

    public BufferedImage getBack() {
        int imageWidth = width * 55 - 5;
        int imageHeight = height * 55 - 5;
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();

        ImageBank imageBank = bot.getMemoImageBank();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (cards[x][y] == -1) continue;

                int posX = x * 55;
                int posY = y * 55;
                imageBank.drawBackground(g, posX, posY);
            }
        }

        return image;
    }

    @Override
    public int getReward(Random random) {
        return random.nextInt(80 + players.length * 10, 251);
    }

    @Override
    public long[] getWinners() {
        return new long[0];
    }

    public int pairsLeft() {
        int left = 0;
        for (int[] row : cards) {
            for (int id : row) {
                if (id >= 0) left++;
            }
        }

        return left / 2;
    }

    public boolean isEmpty() {
        for (int[] row : cards) {
            for (int id : row) {
                if (id >= 0) return false;
            }
        }

        return true;
    }

    public String getScores() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < players.length; i++) {
            if (i > 0) stringBuilder.append('\n');
            stringBuilder.append("<@").append(players[i]).append(">: ").append(scores[i]);
        }

        return stringBuilder.toString();
    }

    public void sendImage(SlashCommandEvent event, String message) throws IOException {
        BufferedImage image = getBack();
        File data = new File("memo1.png");
        ImageIO.write(image, "png", data);

        event.getHook().sendMessage(message).addFile(data).queue();
    }
}
