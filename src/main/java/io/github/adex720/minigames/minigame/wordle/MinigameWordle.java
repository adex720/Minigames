package io.github.adex720.minigames.minigame.wordle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.word.WordManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

// Don't lie, you've heard about wordle
public class MinigameWordle extends Minigame {

    public static final int SQUARE_SIZE = 100;
    public static final int HOLE_SIZE = 10;

    public static final int CURVING = 10;
    public static final int CURVING_DOUBLED = CURVING * 2;
    public static final int SQUARE_SIZE_WITHOUT_CURVING = SQUARE_SIZE - CURVING - CURVING;

    public static final int IMAGE_WIDTH = 5 * SQUARE_SIZE + 6 * HOLE_SIZE;
    public static final int IMAGE_HEIGHT = 6 * SQUARE_SIZE + 7 * HOLE_SIZE;

    public static final int LETTER_LIFT = 75;


    public static final int GUESSES_AT_START = 6;

    public static final Color LETTER_CORRECT = new Color(0x148505);
    public static final Color LETTER_WRONG_POSITION = new Color(0xd1ca04);
    public static final Color LETTER_NON_PRESENT = new Color(0x404040);
    public static final Color LETTER_COLOR = new Color(0xFFFFFF);

    private final String word;
    private final ArrayList<String> guesses;

    public MinigameWordle(MinigamesBot bot, long id, boolean isParty, long lastActive, String word, ArrayList<String> guesses) {
        super(bot, bot.getMinigameTypeManager().WORDLE, id, isParty, lastActive);
        this.word = word;

        this.guesses = guesses;
    }

    public MinigameWordle(CommandInfo ci) {
        this(ci.bot(), ci.gameId(), ci.isInParty(), System.currentTimeMillis(), getWord(ci.bot().getWordManager()), new ArrayList<>());
    }

    public static MinigameWordle start(SlashCommandEvent event, CommandInfo ci) {
        MinigameWordle minigame = new MinigameWordle(ci);

        event.getHook().sendMessage("You started a new game of wordle.").queue();

        return minigame;
    }

    public static MinigameWordle start(ButtonClickEvent event, CommandInfo ci) {
        MinigameWordle minigame = new MinigameWordle(ci);

        event.reply("You started a new game of wordle.").queue();

        return minigame;
    }

    public static String getWord(WordManager wordManager) {
        return wordManager.getWordForWordle();
    }

    public void guess(SlashCommandEvent event, CommandInfo ci) throws IOException {
        active();

        String guess = event.getOption("word").getAsString().toLowerCase(Locale.ROOT);

        if (guess.length() != 5) {
            event.getHook().sendMessage("Word must be 5 letters long!").queue();
            return;
        }

        if (guess.equals(word)) {
            guesses.add(guess);
            sendImage(event, "Good job! " + guess + " was the word");
            finish(event, ci, true);
            return;
        }

        if (!bot.getWordManager().isValidWordForWordle(guess)) {
            event.getHook().sendMessage("That is not a valid English word. If you think it is, make sure it's written correct. If it is written correct you can suggest it to be added on the support server.").queue();
            return;
        }

        guesses.add(guess);

        if (guesses.size() < GUESSES_AT_START) {
            sendImage(event, guess + " was not the word.");
            return;
        }

        sendImage(event, guess + "was not the word. You ran out of guesses. The word was " + word + ".");
    }

    /**
     * Sends the progress image attached to the given message.
     */
    public void sendImage(SlashCommandEvent event, String message) throws IOException {
        File image = new File("wordle.png");
        ImageIO.write(getImage(), "png", image);
        event.getHook().sendMessage(message).addFile(image).queue();
    }

    /**
     * @return Background color for the letter on the given position
     */
    public Color getLetterColor(char letter, int letterPos, String guess) {
        int lettersOnWord = getLetterAmount(letter); // amount of guessed letter on game word;
        int lettersOnGuess = getLetterAmount(letter, guess); // amount of guessed letter on guess;

        if (lettersOnWord == 0) return LETTER_NON_PRESENT;

        if (word.charAt(letterPos) == letter) return LETTER_CORRECT;

        if (lettersOnWord == 1 && lettersOnGuess == 1) {
            return LETTER_WRONG_POSITION;
        }


        if (lettersOnGuess <= lettersOnWord)
            return LETTER_WRONG_POSITION; // guess has the letter more times than the word -> all of that letter are yellow


        int onWordAtWrongPlace = 0;
        int lettersBeforeOnGuess = 0;
        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == letter && !(guess.charAt(i) == letter)) onWordAtWrongPlace++;

            else if (guess.charAt(i) == letter && !(word.charAt(i) == letter) && i < letterPos) lettersBeforeOnGuess++;
        }

        // Example: word is 'crate'. Guess is 'never'.
        if (onWordAtWrongPlace <= lettersBeforeOnGuess) return LETTER_NON_PRESENT; // This is reached on the second 'e'.
        return LETTER_WRONG_POSITION; // This is reached on the first 'e'.
    }

    /**
     * @return amount of the given letter on the guessing word.
     */
    public int getLetterAmount(char letter) {
        int amount = 0;
        for (int i = 0; i < 5; i++) {
            if (word.charAt(i) == letter) amount++;
        }
        return amount;
    }

    /**
     * @return amount of the given letter on {@param guess}.
     */
    public int getLetterAmount(char letter, String guess) {
        int amount = 0;
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == letter) amount++;
        }
        return amount;
    }


    @Override
    public String quit() {
        super.quit();
        return "You quit your previous wordle game. The word was " + word + " and you had " + (GUESSES_AT_START - guesses.size()) + " guesses left.";
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "wordle");

        json.addProperty("id", id);
        json.addProperty("word", word);

        json.addProperty("active", lastActive);
        json.addProperty("party", isParty);

        if (guesses.size() > 0) {
            JsonArray guessesJson = new JsonArray();
            guesses.forEach(guessesJson::add);

            json.add("guesses", guessesJson);
        }

        return json;
    }

    public static MinigameWordle fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        String word = JsonHelper.getString(json, "word");

        long lastActive = JsonHelper.getLong(json, "active");
        boolean isParty = JsonHelper.getBoolean(json, "party");

        JsonArray guessesJson = JsonHelper.getJsonArray(json, "guesses", new JsonArray());
        ArrayList<String> guesses = JsonHelper.JsonArrayToStringArrayList(guessesJson);

        return new MinigameWordle(bot, id, isParty, lastActive, word, guesses);
    }

    /**
     * @return the image containing guesses words with color background hints.
     */
    public BufferedImage getImage() {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics graphics = image.getGraphics();
        graphics.setFont(new Font("Monospaced", Font.PLAIN, 100));

        int guessesCount = guesses.size();

        for (int i = 0; i < GUESSES_AT_START; i++) {
            if (i < guessesCount) {
                drawWord(graphics, i, guesses.get(i));
            } else {
                fillEmptyRow(graphics, i);
            }
        }
        graphics.dispose();
        return image;
    }

    /**
     * @param letterId index of letter on word.
     * @param wordId   index of word on guesses from end.
     */
    private void drawBase(Graphics g, int letterId, int wordId, Color color) {
        g.setColor(color);

        // calculate needed values
        int x = letterId * SQUARE_SIZE + (letterId + 1) * HOLE_SIZE;
        int y = wordId * SQUARE_SIZE + (wordId + 1) * HOLE_SIZE;

        int curvingXFirst = x + CURVING; // Point where curving ends on left side
        int curvingXSecond = x + SQUARE_SIZE - CURVING; // Point where curving starts on right side
        int curvingXInside = curvingXSecond - CURVING; // Point where the left most point of the rounding circle on right is

        int curvingYFirst = y + CURVING;
        int curvingYSecond = y + SQUARE_SIZE - CURVING;
        int curvingYInside = curvingYSecond - CURVING;

        // draw thick cross
        g.fillRect(curvingXFirst, y, SQUARE_SIZE_WITHOUT_CURVING, SQUARE_SIZE); // vertical line
        g.fillRect(x, curvingYFirst, CURVING, SQUARE_SIZE_WITHOUT_CURVING); // left part
        g.fillRect(curvingXSecond, curvingYFirst, CURVING, SQUARE_SIZE_WITHOUT_CURVING); // right part

        // draw circular edges
        g.fillArc(x, y, CURVING_DOUBLED, CURVING_DOUBLED, 90, 90); // top left
        g.fillArc(curvingXInside, y, CURVING_DOUBLED, CURVING_DOUBLED, 0, 90); // top right
        g.fillArc(curvingXInside, curvingYInside, CURVING_DOUBLED, CURVING_DOUBLED, 270, 90); // bottom right
        g.fillArc(x, curvingYInside, CURVING_DOUBLED, CURVING_DOUBLED, 180, 90); // bottom left
    }

    /**
     * Draws the letter on the specific location.
     *
     * @param letterId id of the letter on the word
     * @param wordId   id of the word starting from first guess
     * @param color    background color
     */
    private void drawLetter(Graphics g, int letterId, int wordId, Color color, char letter) {
        drawBase(g, letterId, wordId, color);
        g.setColor(LETTER_COLOR);

        int squareX = letterId * SQUARE_SIZE + (letterId + 1) * HOLE_SIZE; // left most point of square
        int squareY = wordId * SQUARE_SIZE + (wordId + 1) * HOLE_SIZE; // bottom most point of square

        int letterWidth = g.getFontMetrics().charWidth(letter);

        int startX = squareX + (SQUARE_SIZE - letterWidth) / 2;
        int startY = squareY + LETTER_LIFT;
        g.drawString(Character.toString(letter), startX, startY);
    }

    /**
     * Draws the word on the given height with correct background colors.
     */
    private void drawWord(Graphics graphics, int wordId, String word) {
        for (int i = 0; i < 5; i++) {
            char letter = word.charAt(i);
            drawLetter(graphics, i, wordId, getLetterColor(letter, i, word), letter);
        }
    }

    /**
     * Fills the row on given height with gray background tiles.
     */
    private void fillEmptyRow(Graphics graphics, int rowId) {
        for (int i = 0; i < 5; i++) {
            drawBase(graphics, i, rowId, LETTER_NON_PRESENT);
        }
    }

}
