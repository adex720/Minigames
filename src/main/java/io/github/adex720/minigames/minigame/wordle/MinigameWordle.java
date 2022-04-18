package io.github.adex720.minigames.minigame.wordle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.word.WordManager;
import io.github.adex720.minigames.minigame.Minigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Don't lie, you've heard about wordle
public class MinigameWordle extends Minigame {

    public static final int SQUARE_SIZE = 100;
    public static final int SQUARE_HOLE_SIZE = 10;

    public static final int CURVING = 10;
    public static final int CURVING_DOUBLED = CURVING * 2;
    public static final int SQUARE_SIZE_WITHOUT_CURVING = SQUARE_SIZE - CURVING_DOUBLED;

    public static final int IMAGE_WIDTH = 5 * SQUARE_SIZE + 6 * SQUARE_HOLE_SIZE;
    public static final int IMAGE_HEIGHT = 6 * SQUARE_SIZE + 7 * SQUARE_HOLE_SIZE;

    public static final int LETTER_LIFT = 75;

    public static final int KEY_SIZE = 70;
    public static final int HALF_KEY_SIZE = KEY_SIZE / 2;
    public static final int KEY_LETTER_LIFT = 55;


    public static final int GUESSES_AT_START = 6;

    public static final Color LETTER_CORRECT = new Color(0x148505);
    public static final Color LETTER_WRONG_POSITION = new Color(0xd1ca04);
    public static final Color LETTER_NON_PRESENT = new Color(0x404040);
    public static final Color LETTER_UNKNOWN = new Color(0xB0B0B0);
    public static final Color LETTER_COLOR = new Color(0xFFFFFF);

    private final String word;
    private final ArrayList<String> guesses;

    private boolean isWordlistUsed;

    public MinigameWordle(MinigamesBot bot, long id, boolean isParty, long lastActive, String word, ArrayList<String> guesses) {
        super(bot, bot.getMinigameTypeManager().WORDLE, id, isParty, lastActive);
        this.word = word;

        this.guesses = guesses;
        isWordlistUsed = false;
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
        active(ci);
        Replyable replyable = Replyable.from(event);

        String guess = event.getOption("word").getAsString().toLowerCase(Locale.ROOT);

        if (guess.length() != 5) {
            replyable.reply("Word must be 5 letters long!");
            return;
        }

        if (guess.equals(word)) {
            guesses.add(guess);
            sendImage(event, "Good job! " + guess + " was the word");
            finish(replyable, ci, true);
            return;
        }

        if (!bot.getWordManager().isValidWordForWordle(guess)) {
            replyable.reply("That is not a valid English word. If you think it is, make sure it's written correct. If it is written correct you can suggest it to be added on the support server.");
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
    public String quit(@Nullable Replyable replyable) {
        super.quit(replyable);
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
        ArrayList<String> guesses = JsonHelper.jsonArrayToStringArrayList(guessesJson);

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
        int x = letterId * SQUARE_SIZE + (letterId + 1) * SQUARE_HOLE_SIZE;
        int y = wordId * SQUARE_SIZE + (wordId + 1) * SQUARE_HOLE_SIZE;

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

        int squareX = letterId * SQUARE_SIZE + (letterId + 1) * SQUARE_HOLE_SIZE; // left most point of square
        int squareY = wordId * SQUARE_SIZE + (wordId + 1) * SQUARE_HOLE_SIZE; // bottom most point of square

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

    /**
     * Returns the correct background color for the letter to be shown on keyboard.
     */
    public Color getBackgroundColorForLetter(char letter) {
        if (guesses.isEmpty()) return LETTER_UNKNOWN; // no guesses yet
        boolean inWord = word.indexOf(letter) >= 0;

        boolean included = false;

        for (String guess : guesses) {
            int firstOccurrence = guess.indexOf(letter);
            if (firstOccurrence == -1) continue;

            for (int i = firstOccurrence; i < 5; i++) {
                char checkingLetter = guess.charAt(i);

                if (checkingLetter != letter) continue;

                if (!inWord) return LETTER_NON_PRESENT;

                if (word.charAt(i) == letter) return LETTER_CORRECT;
                included = true; // at wrong place on this guess
            }
        }

        return included ? LETTER_WRONG_POSITION : LETTER_UNKNOWN;
    }

    public boolean isWordlistUsed() {
        return isWordlistUsed;
    }

    public void sendWordList(SlashCommandEvent event, String format) {
        if (isWordlistUsed()) {
            event.getHook().sendMessage("You have already used wordlist on this game!").queue();
            return;
        }

        String error = isFormatValid(format);
        if (error != null) {
            event.getHook().sendMessage(error).queue();
            return;
        }

        event.getHook().sendMessageEmbeds(getWordlist(format, event.getUser())).queue();
    }

    /**
     * @param format format to analyze
     * @return error message for invalid format. Returns null if format is valid.
     */
    @Nullable
    public String isFormatValid(String format) {
        if (guesses.size() < 2) return "You need to have guessed at least two times to use this command!";

        if (format.length() != 5) return "Length must be five";

        format = format.toLowerCase(Locale.ROOT);
        int unknownPositions = 0;
        for (int i = 0; i < 5; i++) {
            char letter = format.charAt(i);

            if (letter == '?') {
                unknownPositions++;
                continue;
            }

            if (letter < 'a' || letter > 'z') {
                return "String contains illegal characters. Only English letters and ?-mark are allowed!";
            }
        }

        if (unknownPositions == 0) return "You need to include at least one unknown letter!";
        if (unknownPositions > 3) return "You need to include at least 2 known letters!";

        return null;
    }

    /**
     * If more than 20 matches exists, first 20 are displayed.
     *
     * @return {@link MessageEmbed} containing valid words.
     */
    public MessageEmbed getWordlist(String format, User user) {
        StringBuilder words = new StringBuilder();
        int count = 0;

        char letterFirst = format.charAt(0);
        char letterSecond = format.charAt(1);
        char letterThird = format.charAt(2);
        char letterFourth = format.charAt(3);
        char letterFifth = format.charAt(4);

        boolean unknownFirst = letterFirst == '?';
        boolean unknownSecond = letterSecond == '?';
        boolean unknownThird = letterThird == '?';
        boolean unknownFourth = letterFourth == '?';
        boolean unknownFifth = letterFifth == '?';

        for (String word : bot.getWordManager().getLengthOf5()) {
            if (unknownFirst || word.charAt(0) == letterFirst) { // check if word matches pattern
                if (unknownSecond || word.charAt(1) == letterSecond) {
                    if (unknownThird || word.charAt(2) == letterThird) {
                        if (unknownFourth || word.charAt(3) == letterFourth) {
                            if (unknownFifth || word.charAt(4) == letterFifth) {
                                count++;
                                words.append(word);
                                words.append("\n");
                                if (count == 20) break;
                            }
                        }
                    }
                }
            }
        }

        if (count == 0) words.append("No words match your search!");
        if (count == 20) words.append("\nAt least 20 words apply your search. Only 20 first results are included.");

        words.append("\nYou can't use this command again on this game!");

        isWordlistUsed = true; // Wordlist can only be used once per game!

        return new EmbedBuilder()
                .setTitle("WORDLIST")
                .setColor(type.color)
                .addField("Words matching pattern `" + format + "`", words.toString(), false)
                .setFooter(user.getName(), user.getAvatarUrl())
                .setTimestamp(new Date().toInstant())
                .build();

    }

    public BufferedImage getLetters() {
        BufferedImage image = new BufferedImage(645, 220, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = image.getGraphics();
        g.setFont(new Font("Helvetica", Font.PLAIN, 64));

        for (char letter = 'a'; letter <= 'z'; letter++) {
            int[] position = getKeyboardPosition(letter);
            g.setColor(getBackgroundColorForLetter(letter));
            g.fillRect(position[0], position[1], KEY_SIZE, KEY_SIZE);

            g.setColor(LETTER_COLOR);
            int letterX = (int) (position[0] + HALF_KEY_SIZE - g.getFontMetrics().charWidth(letter) * 0.5f);
            int letterY = position[1] + KEY_LETTER_LIFT;
            g.drawString(Character.toString(letter), letterX, letterY);
        }

        return image;
    }

    /**
     * Returns an int array with length of 2.
     * This first entry is x location and second is y.
     * The distance from any side to the same side on the nextUpperCase key is 75 pixels.
     * Returns {-1, -1} on invalid key.
     */
    public int[] getKeyboardPosition(char key) {
        return switch (key) {
            case 'q' -> new int[]{0, 0};
            case 'w' -> new int[]{75, 0};
            case 'e' -> new int[]{150, 0};
            case 'r' -> new int[]{225, 0};
            case 't' -> new int[]{300, 0};
            case 'y' -> new int[]{375, 0};
            case 'u' -> new int[]{450, 0};
            case 'i' -> new int[]{525, 0};
            case 'o' -> new int[]{600, 0};
            case 'p' -> new int[]{675, 0};

            case 'a' -> new int[]{19, 75};
            case 's' -> new int[]{94, 75};
            case 'd' -> new int[]{169, 75};
            case 'f' -> new int[]{244, 75};
            case 'g' -> new int[]{319, 75};
            case 'h' -> new int[]{394, 75};
            case 'j' -> new int[]{469, 75};
            case 'k' -> new int[]{544, 75};
            case 'l' -> new int[]{619, 75};

            case 'z' -> new int[]{56, 150};
            case 'x' -> new int[]{131, 150};
            case 'c' -> new int[]{206, 150};
            case 'v' -> new int[]{281, 150};
            case 'b' -> new int[]{356, 150};
            case 'n' -> new int[]{431, 150};
            case 'm' -> new int[]{506, 150};

            default -> new int[]{-1, -1};
        };
    }

}
