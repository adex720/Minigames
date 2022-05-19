package io.github.adex720.minigames.minigame.duel.tictactoe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.duel.DuelMinigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Random;

/**
 * @author adex720
 */
public class MinigameTicTacToe extends DuelMinigame {

    public static final int ROW_TOP = 1;
    public static final int ROW_MIDDLE = 2;
    public static final int ROW_BOTTOM = 3;
    public static final int COLUMN_LEFT = 4;
    public static final int COLUMN_MIDDLE = 5;
    public static final int COLUMN_RIGHT = 6;
    public static final int DIAGONAL_LEFT_TOP = 7;
    public static final int DIAGONAL_RIGHT_TOP = 8;

    private final char[][] board;

    public String lastAIMove;

    public MinigameTicTacToe(MinigamesBot bot, MinigameTypeManager typeManager, long id, long lastActive) {
        super(bot, typeManager.TIC_TAC_TOE, id, Util.MINIGAMES_BOT_ID, false, lastActive);

        board = new char[3][3];
        fillBoard();

        if (!isFirstPlayersTurn) {
            makeAIMove();
            isFirstPlayersTurn = true;

        }
    }

    public MinigameTicTacToe(MinigamesBot bot, MinigameTypeManager typeManager, long id, long opponentId, long lastActive) {
        super(bot, typeManager.TIC_TAC_TOE, id, opponentId, true, lastActive);

        board = new char[3][3];
        fillBoard();
    }

    public MinigameTicTacToe(MinigamesBot bot, MinigameTypeManager typeManager, long id, long lastActive, char[][] board) {
        this(bot, typeManager, id, lastActive);
        fillBoard(board);
    }

    public MinigameTicTacToe(MinigamesBot bot, MinigameTypeManager typeManager, long id, long opponentId, long lastActive, char[][] board, boolean isFirstPlayersTurn) {
        this(bot, typeManager, id, opponentId, lastActive);
        fillBoard(board);
        this.isFirstPlayersTurn = isFirstPlayersTurn;
    }

    /**
     * Fills the board 2d array with given board
     */
    private void fillBoard(char[][] board) {
        for (short x = 0; x < 3; x++) {
            System.arraycopy(board[x], 0, this.board[x], 0, 3);
        }
    }

    public static MinigameTicTacToe start(SlashCommandInteractionEvent event, CommandInfo ci) {
        MinigameTicTacToe minigame = null;
        if (ci.isInParty()) {
            if (ci.party().getMembersWithoutOwner().size() == 1) {
                minigame = new MinigameTicTacToe(ci.bot(), ci.bot().getMinigameTypeManager(), ci.authorId(), ci.party().getMemberId(), System.currentTimeMillis());
            }
        } else {
            minigame = new MinigameTicTacToe(ci.bot(), ci.bot().getMinigameTypeManager(), ci.authorId(), System.currentTimeMillis());
        }

        if (minigame != null) {
            event.getHook().sendMessage("You started a new game of Tic Tac Toe against <@!" + minigame.opponentId + ">.").queue();
            event.getHook().sendMessageEmbeds(minigame.getEmbed()).queue();
        }

        return minigame;
    }

    public static MinigameTicTacToe start(ButtonInteractionEvent event, CommandInfo ci) {
        MinigameTicTacToe minigame = null;
        if (ci.isInParty()) {
            if (ci.party().getMembersWithoutOwner().size() == 1) {
                minigame = new MinigameTicTacToe(ci.bot(), ci.bot().getMinigameTypeManager(), ci.authorId(), ci.party().getMemberId(), System.currentTimeMillis());
            }
        } else {
            minigame = new MinigameTicTacToe(ci.bot(), ci.bot().getMinigameTypeManager(), ci.authorId(), System.currentTimeMillis());
        }

        if (minigame != null) {
            event.getHook().sendMessage("You started a new game of Tic Tac Toe against <@!" + minigame.opponentId + ">.").queue();
            event.getHook().sendMessageEmbeds(minigame.getEmbed()).queue();
        }

        return minigame;
    }

    /**
     * Fills the board 2d array with spaces.
     */
    private void fillBoard() {
        for (short x = 0; x < 3; x++) {
            for (short y = 0; y < 3; y++) {
                board[x][y] = ' ';
            }
        }
    }

    @Override
    public int getReward(Random random) {
        int spaceLeft = getAmountOfEmptySpots();

        // A game can't end with 5 or more free spaces
        int min, max;

        if (spaceLeft == 0) {
            min = 175;
        } else {
            min = 150;
        }

        if (spaceLeft <= 2) {
            max = 250;
        } else if (spaceLeft == 3) {
            max = 200;
        } else {
            max = 175;
        }

        return random.nextInt(min, max + 1);
    }

    /**
     * Since the x and y positions only require 2 bits to be stored each,
     * The are stored on the same variable.
     * <p>
     * This method converts x and y values to one short.
     */
    public static short compact(short x, short y) {
        return (short) ((x << 2) | y);
    }

    public static short getXFromCompacted(short compacted) {
        return (short) ((compacted >> 2) & 0x3);
    }

    public static String getPositionName(short position) {
        return getPositionName(getXFromCompacted(position), getYFromCompacted(position));
    }

    public static String getPositionName(short x, short y) {
        if (x != 1) {
            if (y != 1) {
                return getColumnName(x) + " " + getRowName(y);
            } else {
                return "middle " + getColumnName(x);
            }
        } else {
            if (y != 1) {
                return "middle " + getRowName(y);
            } else {
                return "the middle";
            }
        }
    }

    public static short getYFromCompacted(short compacted) {
        return (short) (compacted & 0x3);
    }

    public static String getColumnName(short x) {
        return switch (x) {
            case 0 -> "left";
            case 1 -> "middle";
            case 2 -> "right";
            default -> throw new IllegalStateException("Unexpected column: " + x);
        };
    }

    public static String getRowName(short y) {
        return switch (y) {
            case 0 -> "top";
            case 1 -> "middle";
            case 2 -> "bottom";
            default -> throw new IllegalStateException("Unexpected row: " + y);
        };
    }

    public void set(SlashCommandInteractionEvent event, CommandInfo ci) {
        long setter = ci.authorId();
        Replyable replyable = Replyable.from(event);

        if (setter == id) {
            if (!isFirstPlayersTurn) {
                replyable.reply("It is not your turn!");
                return;
            }
        } else if (setter == opponentId) {
            if (isFirstPlayersTurn) {
                replyable.reply("It is not your turn!");
                return;
            }
        } else {
            replyable.reply("You are not part of this game.");
            return;
        }

        active(ci);

        short x = (short) event.getOption("column").getAsLong();
        short y = (short) event.getOption("row").getAsLong();

        if (isOccupied(x, y)) {
            replyable.reply(getEmbedWithField("Can't set mark", "That position is already in use."));
            return;
        }

        place(x, y, isFirstPlayersTurn);
        isFirstPlayersTurn = !isFirstPlayersTurn;

        int winner = getWinner();
        if (winner == 0) {
            if (checkForAIMove()) {
                winner = getWinner();
            }
        }

        if (winner == 0) {
            replyable.reply(getEmbedWithField("Set your mark",
                    "You set your mark in " + getPositionName(x, y) + "."
                            + (isParty ? "" : "\nAI set its mark on " + lastAIMove)));
            return;
        }

        if (winner == 3) {
            replyable.reply(getEmbedWithField("The game ended in a draw", "The board is full yet neither of the players has won."));
            finish(replyable, ci, winner);
            return;
        }

        String winnerMention = "<@!" + (winner == FIRST_PLAYER_WON ? id : opponentId) + ">";
        replyable.reply(getEmbedWithField("The game ended", winnerMention + " won the game!"));
        finish(replyable, ci, winner);
    }

    public void place(short x, short y, boolean firstPlayer) {
        board[x][y] = firstPlayer ? 'x' : 'o';
    }

    /**
     * @return 0 if there's no winner,
     * 1 if first player is the winner,
     * 2 if second player is the winner and
     * 3 if the game is draw.
     */
    public int getWinner() {
        for (short line = ROW_TOP; line <= DIAGONAL_RIGHT_TOP; line++) {
            short score = getLineScore(line);
            if (score == 3) return FIRST_PLAYER_WON;
            if (score == -3) return SECOND_PLAYER_WON;
        }

        return doesBoardHaveEmptySpots() ? UNFINISHED : DRAW;
    }

    public boolean isOccupied(short x, short y) {
        return board[x][y] != ' ';
    }

    public boolean doesBoardHaveEmptySpots() {
        for (short x = 0; x < 3; x++) {
            for (short y = 0; y < 3; y++) {
                if (board[x][y] == ' ') return true;
            }
        }

        return false;
    }

    public int getAmountOfEmptySpots() {
        int amount = 0;
        for (short x = 0; x < 3; x++) {
            for (short y = 0; y < 3; y++) {
                if (board[x][y] == ' ') amount++;
            }
        }
        return amount;
    }

    @Override
    public EmbedBuilder getEmbedBase() {
        SelfUser selfUser = bot.getJda().getSelfUser();
        return new EmbedBuilder()
                .setTitle("TIC TAC TOE")
                .setColor(type.color)
                .addField("Board", getBoard(), true)
                .addField("Players", "**X** <@!" + id + ">\n**O** <@!" + opponentId + ">", true)
                .setFooter(selfUser.getName(), selfUser.getAvatarUrl())
                .setTimestamp(new Date().toInstant());
    }

    /**
     * Returns the board with horizontal lines between the rows
     */
    public String getBoard() {
        short zero = 0;
        short one = 1;
        short two = 2;

        return "|" + getMark(zero, zero) + "|" + getMark(one, zero) + "|" + getMark(two, zero) + "|\n" +
                "|" + getMark(zero, one) + "|" + getMark(one, one) + "|" + getMark(two, one) + "|\n" +
                "|" + getMark(zero, two) + "|" + getMark(one, two) + "|" + getMark(two, two) + "|";
    }

    /**
     * @return 'X', 'O' or ' '
     */
    public String getMark(short x, short y) {
        char mark = board[x][y];
        if (mark == ' ') return " ";
        if (mark == 'x') return "X";
        return "O";
    }

    /**
     * @param line the line to check.
     * @return the amount of marks the first player has on the row with the amount of marks of the second players reduced from it.
     */
    public short getLineScore(int line) {
        short score = 0;
        for (char mark : getLine(line)) {
            if (mark == 'x') score++;
            else if (mark == 'o') score--;
        }
        return score;
    }

    /**
     * @return returns a compacted short containing the position of the first free tile on the given line.
     * If the line has no free spaces this returns -1.
     */
    public short getFirstFreeOnLine(int line) {
        short zero = 0;
        short one = 1;
        short two = 2;

        if (board[0][0] == ' ') {
            if (line == ROW_TOP || line == COLUMN_LEFT || line == DIAGONAL_LEFT_TOP) {
                return compact(zero, zero);
            }
        }
        if (board[1][0] == ' ') {
            if (line == ROW_TOP || line == COLUMN_MIDDLE) {
                return compact(one, zero);
            }

        }
        if (board[2][0] == ' ') {
            if (line == ROW_TOP || line == COLUMN_RIGHT || line == DIAGONAL_RIGHT_TOP) {
                return compact(two, zero);
            }

        }
        if (board[0][1] == ' ') {
            if (line == ROW_MIDDLE || line == COLUMN_LEFT) {
                return compact(zero, one);
            }

        }
        if (board[1][1] == ' ') {
            if (line == ROW_MIDDLE || line == COLUMN_MIDDLE || line == DIAGONAL_LEFT_TOP || line == DIAGONAL_RIGHT_TOP) {
                return compact(one, one);
            }

        }
        if (board[2][1] == ' ') {
            if (line == ROW_MIDDLE || line == COLUMN_RIGHT) {
                return compact(two, one);
            }

        }
        if (board[0][2] == ' ') {
            if (line == ROW_BOTTOM || line == COLUMN_LEFT || line == DIAGONAL_RIGHT_TOP) {
                return compact(zero, two);
            }

        }
        if (board[1][2] == ' ') {
            if (line == ROW_BOTTOM || line == COLUMN_MIDDLE) {
                return compact(one, two);
            }

        }
        if (board[2][2] == ' ') {
            if (line == ROW_BOTTOM || line == COLUMN_RIGHT || line == DIAGONAL_LEFT_TOP) {
                return compact(two, two);
            }
        }

        return -1;
    }

    public char[] getLine(int line) {
        return switch (line) {
            case ROW_TOP -> new char[]{board[0][0], board[1][0], board[2][0]};
            case ROW_MIDDLE -> new char[]{board[0][1], board[1][1], board[2][1]};
            case ROW_BOTTOM -> new char[]{board[0][2], board[1][2], board[2][2]};

            case COLUMN_LEFT -> board[0];
            case COLUMN_MIDDLE -> board[1];
            case COLUMN_RIGHT -> board[2];

            case DIAGONAL_LEFT_TOP -> new char[]{board[0][0], board[1][1], board[2][2]};
            case DIAGONAL_RIGHT_TOP -> new char[]{board[0][2], board[1][1], board[2][0]};
            default -> throw new IllegalStateException("Unexpected line: " + line);
        };
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "tic-tac-toe");

        json.addProperty("active", lastActive);
        json.addProperty("duel", isParty);

        JsonArray boardJson = new JsonArray();
        for (char[] column : board) {
            boardJson.add(JsonHelper.arrayToJsonArray(column));
        }
        json.add("board", boardJson);

        if (isParty) {
            json.addProperty("first", id);
            json.addProperty("second", opponentId);
            json.addProperty("turn", isFirstPlayersTurn ? "first" : "second");
        } else {
            json.addProperty("id", id);
        }

        return json;
    }

    public static MinigameTicTacToe fromJson(JsonObject json, MinigamesBot bot) {
        long lastActive = JsonHelper.getLong(json, "active");

        char[][] board = new char[3][3];
        JsonArray boardJson = JsonHelper.getJsonArray(json, "board");
        for (short column = 0; column < 3; column++) {
            board[column] = JsonHelper.jsonArrayToCharArray(boardJson.get(column).getAsJsonArray());
        }

        if (JsonHelper.getBoolean(json, "duel")) {
            long firstId = JsonHelper.getLong(json, "first");
            long secondId = JsonHelper.getLong(json, "second");
            boolean isFirstTurn = JsonHelper.getString(json, "turn").equals("first");

            return new MinigameTicTacToe(bot, bot.getMinigameTypeManager(), firstId, secondId, lastActive, board, isFirstTurn);
        } else {
            long id = JsonHelper.getLong(json, "id");

            return new MinigameTicTacToe(bot, bot.getMinigameTypeManager(), id, lastActive, board);
        }
    }

    @Override
    public String quit(@Nullable Replyable replyable) {
        super.quit(replyable);
        return "You quit your previous game of tic tac toe. You didn't receive any rewards.";
    }
}
