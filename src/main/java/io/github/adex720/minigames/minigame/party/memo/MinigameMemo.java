package io.github.adex720.minigames.minigame.party.memo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.party.Party;
import io.github.adex720.minigames.minigame.party.PartyCompetitiveMinigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;

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

    private boolean turned; // If one card is turned
    private int turnedX; // Value doesn't matter if 'turned' is false
    private int turnedY;
    private AuditableRestAction<Void> turnedMessageDeletion;

    /**
     * If card is not turned, {@param turnedX} should be -1.
     */
    public MinigameMemo(MinigamesBot bot, long id, long lastActive, int[][] cards, long[] players, int currentPlayerIndex, int[] scores, int turnedX, int turnedY) {
        super(bot, bot.getMinigameTypeManager().MEMO, id, lastActive);

        this.cards = cards;
        this.width = cards.length;
        this.height = cards[0].length;

        this.players = players;
        this.currentPlayerIndex = currentPlayerIndex;

        this.scores = scores;

        onBlock = false;

        this.turned = turnedX >= 0;
        this.turnedX = turnedX;
        this.turnedY = turnedY;
        this.turnedMessageDeletion = null;
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

    public static MinigameMemo start(Replyable replyable, CommandInfo commandInfo) {
        MinigameMemo minigame = new MinigameMemo(commandInfo);
        replyable.reply("You started a new game of memo.");
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

        int turnedX = JsonHelper.getInt(json, "turned_x", -1);
        int turnedY = JsonHelper.getInt(json, "turned_y", -1);

        return new MinigameMemo(bot, id, active, cards, players, currentPlayerIndex, scores, turnedX, turnedY);
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

        if (turned) {
            json.addProperty("turned_x", turnedX);
            json.addProperty("turned_y", turnedY);
        }

        return json;
    }

    public void turn(SlashCommandInteractionEvent event, CommandInfo ci) throws IOException {
        long userId = players[currentPlayerIndex];
        if (ci.authorId() != userId) {
            event.getHook().sendMessage("It is not your turn!").setEphemeral(true).queue();
            return;
        }

        if (onBlock) {
            event.getHook().sendMessage("You can't turn cards right now.").setEphemeral(true).queue();
            return;
        }

        active(ci);
        int x = event.getOption("column").getAsInt();
        int y = event.getOption("row").getAsInt();

        if (x >= width || x < 0 || y >= height || y < 0) {
            event.getHook().sendMessage("The card is outside the board!").queue();
            return;
        }

        if (turned) {
            if (x == turnedX && y == turnedY) {
                event.getHook().sendMessage("That card is already turned").queue();
                return;
            }

            turnSecond(event, ci, x, y);
            return;
        }

        turnFirst(event, x, y);
    }

    /**
     * Turns the card as it is the first card of the turn.
     *
     * @param event event to reply to.
     * @param x     column
     * @param y     row
     */
    public void turnFirst(SlashCommandInteractionEvent event, int x, int y) throws IOException {
        turnedX = x;
        turnedY = y;

        BufferedImage image = getImage(x, y);
        File data = new File("memo" + id + ".png");
        ImageIO.write(image, "png", data); // write card image to file

        turned = true;
        event.getHook().sendMessage("You turned a card").addFile(data).queue(m -> turnedMessageDeletion = m.delete());
    }

    /**
     * Turns the card as it is the second card of the turn.
     *
     * @param event event to reply to.
     * @param ci    Command Info
     * @param x     column
     * @param y     row
     */
    public void turnSecond(SlashCommandInteractionEvent event, CommandInfo ci, int x, int y) throws IOException {
        int id1 = cards[turnedX][turnedY];
        int id2 = cards[x][y];
        if (id2 == -1) {
            event.getHook().sendMessage("The tile is empty!").queue();
            return;
        }

        if (id1 == id2) {
            onPairFound(event, ci, x, y);
        } else {
            onWrongPair(event, x, y);
        }
        turnedMessageDeletion.queue(); // Delete message showing first turned card.

        turned = false;
    }

    /**
     * Updates variables and stats.
     * Sends correct messages and images.
     */
    public void onPairFound(SlashCommandInteractionEvent event, CommandInfo ci, int x, int y) throws IOException {
        BufferedImage image = getImage(turnedX, turnedY, x, y); // get data before changing variables
        File data = new File("memo" + id + ".png");
        ImageIO.write(image, "png", data); // write card image to file

        scores[currentPlayerIndex]++; // append score
        cards[turnedX][turnedY] = -1; // remove cards from board
        cards[x][y] = -1;
        ci.profile().increaseStat("memo pairs found");

        if (isEmpty()) {
            event.getHook().sendMessage("All pairs were found! The scores are:\n" + getScores()).queue();
            finish(Replyable.from(event), ci, true);
            return;
        }

        event.getHook().sendMessage("You found a pair and got another turn! The scores are:\n" + getScores())
                .addFile(data).queue();

    }

    /**
     * Updates variables and stats.
     * Sends correct messages and images.
     */
    public void onWrongPair(SlashCommandInteractionEvent event, int x, int y) throws IOException {
        onBlock = true;
        currentPlayerIndex++;
        if (currentPlayerIndex == players.length) currentPlayerIndex = 0;

        BufferedImage image = getImage(turnedX, turnedY, x, y);
        File data = new File("memo" + id + ".png");
        ImageIO.write(image, "png", data); // write card image to file

        event.getHook().sendMessage("You didn't find a pair.").addFile(data)
                .delay(Duration.ofSeconds(5))
                .flatMap(Message::delete).queue(); // Delete image after 5 seconds.

        BufferedImage background = getBack();
        File backgroundData = new File("memo2" + id + ".png");
        ImageIO.write(background, "png", backgroundData);
        event.getHook().sendMessage("<@" + players[currentPlayerIndex] + ">, It's soon your turn!")
                .delay(Duration.ofSeconds(6))
                .flatMap(m -> m.editMessage("<@" + players[currentPlayerIndex] + ">, It's now your turn!")
                        .addFile(backgroundData)) // Add image to message after 6 seconds
                .queue(m -> onBlock = false); // Remove block
    }

    /**
     * Creates an image of the current cards with 2 of them being turned.
     *
     * @param x1 column of first card.
     * @param y1 row of first card.
     * @param x2 column of second card.
     * @param y2 row of second card.
     */
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

    /**
     * Creates an image of the current cards with 1 of them being turned.
     *
     * @param column column of trh card.
     * @param row    row of trh card.
     */
    public BufferedImage getImage(int column, int row) {
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
                if (column == x && row == y) {
                    imageBank.drawCard(g, posX, posY, cards[x][y]);
                    continue;
                }

                imageBank.drawBackground(g, posX, posY);
            }
        }

        return image;
    }

    /**
     * Creates an image of the current cards, each one being background up.
     */
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

    /**
     * ONLY FOR DEBUG USE!!!
     * <p>
     * Creates the image of the cards with each of them being face up.
     */
    private BufferedImage getImage() {
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

                imageBank.drawCard(g, posX, posY, cards[x][y]);

            }
        }

        return image;
    }

    @Override
    public int getReward(Random random) { // Better average reward for higher amount of palyers
        return random.nextInt(80 + players.length * 10, 251);
    }

    @Override
    public Long[] getWinners() {
        int max = 0;
        ArrayList<Long> winners = new ArrayList<>(players.length);

        for (int i = 0; i < players.length; i++) {
            int score = scores[i];
            if (score > max) {
                max = score;
                winners.clear();
                winners.add(players[i]);
            } else if (score == max) {
                winners.add(players[i]);
            }
        }

        return winners.toArray(new Long[0]);
    }

    /**
     * Calculates the amount of pairs left.
     */
    public int pairsLeft() {
        int left = 0;
        for (int[] row : cards) {
            for (int id : row) {
                if (id >= 0) left++;
            }
        }

        return left / 2;
    }

    /**
     * Checks if cards remain.
     */
    public boolean isEmpty() {
        for (int[] row : cards) {
            for (int id : row) {
                if (id >= 0) return false;
            }
        }

        return true;
    }

    /**
     * Returns the scores of each player on their own rows.
     * The order matches the turn order.
     */
    public String getScores() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < players.length; i++) {
            if (i > 0) stringBuilder.append('\n');
            stringBuilder.append("<@").append(players[i]).append(">: ").append(scores[i]);
        }

        return stringBuilder.toString();
    }

    /**
     * Sends the current cards backgrounds with the given message.
     *
     * @param event   event to reply to.
     * @param message Message to send the image with.
     */
    public void sendImage(SlashCommandInteractionEvent event, String message) throws IOException {
        BufferedImage image = getBack();
        File data = new File("memo" + id + ".png");
        ImageIO.write(image, "png", data);

        event.getHook().sendMessage(message).addFile(data).queue();
    }
}
