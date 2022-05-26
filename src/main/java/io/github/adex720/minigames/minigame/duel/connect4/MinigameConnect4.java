package io.github.adex720.minigames.minigame.duel.connect4;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.duel.DuelMinigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.replyable.Replyable;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Random;

public class MinigameConnect4 extends DuelMinigame {

    private final char[][] board;

    public MinigameConnect4(MinigamesBot bot, MinigameTypeManager typeManager, long id, long opponentId, long lastActive) {
        super(bot, typeManager.CONNECT4, id, opponentId, false, lastActive);

        board = new char[7][6];
        fillBoard();

        isFirstPlayersTurn = bot.getRandom().nextBoolean();
    }

    public MinigameConnect4(MinigamesBot bot, MinigameTypeManager typeManager, long id, long opponentId, long lastActive, char[][] board, boolean isFirstPlayersTurn) {
        super(bot, typeManager.CONNECT4, id, opponentId, false, lastActive);

        this.board = board;
        this.isFirstPlayersTurn = isFirstPlayersTurn;
    }

    /**
     * Fills the board 2d array with spaces.
     */
    private void fillBoard() {
        for (short x = 0; x < 7; x++) {
            for (short y = 0; y < 6; y++) {
                board[x][y] = ' ';
            }
        }
    }

    public static MinigameConnect4 start(SlashCommandInteractionEvent event, CommandInfo ci) {
        MinigameConnect4 minigame = null;
        if (ci.isInParty()) {
            if (ci.party().getMembersWithoutOwner().size() == 1) {
                minigame = new MinigameConnect4(ci.bot(), ci.bot().getMinigameTypeManager(), ci.gameId(), ci.party().getMemberId(), System.currentTimeMillis());
            }
        }

        if (minigame != null) {
            event.getHook().sendMessage("You started a new game of connect against <@!" + minigame.getDifferentPlayerId(ci.authorId()) + ">.").queue();
            event.getHook().sendMessageEmbeds(minigame.getEmbedWithField("The player starting is:", "<@" + minigame.getCurrentPlayerId() + ">"))
                    .addActionRows(minigame.getButtons()).queue();
        }

        return minigame;
    }

    public static MinigameConnect4 start(ButtonInteractionEvent event, CommandInfo ci) {
        MinigameConnect4 minigame = null;
        if (ci.isInParty()) {
            if (ci.party().getMembersWithoutOwner().size() == 1) {
                minigame = new MinigameConnect4(ci.bot(), ci.bot().getMinigameTypeManager(), ci.gameId(), ci.party().getMemberId(), System.currentTimeMillis());
            }
        }

        if (minigame != null) {
            event.getHook().sendMessage("You started a new game of connect against <@!" + minigame.getDifferentPlayerId(ci.authorId()) + ">.").queue();
            event.getHook().sendMessageEmbeds(minigame.getEmbedWithField("The player starting is:", "<@" + minigame.getCurrentPlayerId() + ">"))
                    .addActionRows(minigame.getButtons()).queue();
        }

        return minigame;
    }

    public long getDifferentPlayerId(long id) {
        if (id == this.id) return opponentId;
        return this.id;
    }

    public static MinigameConnect4 fromJson(JsonObject json, MinigamesBot bot) {
        long firstId = JsonHelper.getLong(json, "first");
        long secondId = JsonHelper.getLong(json, "second");
        boolean isFirstTurn = JsonHelper.getString(json, "turn").equals("first");
        long lastActive = JsonHelper.getLong(json, "active");

        char[][] board = new char[7][6];
        JsonArray boardJson = JsonHelper.getJsonArray(json, "board");
        for (short column = 0; column < 7; column++) {
            board[column] = JsonHelper.jsonArrayToCharArray(boardJson.get(column).getAsJsonArray());
        }

        return new MinigameConnect4(bot, bot.getMinigameTypeManager(), firstId, secondId, lastActive, board, isFirstTurn);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "connect4");

        json.addProperty("first", id);
        json.addProperty("second", opponentId);
        json.addProperty("turn", isFirstPlayersTurn ? "first" : "second");
        json.addProperty("active", lastActive);

        JsonArray boardJson = new JsonArray();
        for (char[] column : board) {
            boardJson.add(JsonHelper.arrayToJsonArray(column));
        }
        json.add("board", boardJson);

        return json;
    }

    @Override
    public int getReward(Random random) {
        return random.nextInt(100, 250);
    }

    /**
     * Drops one mark to the board
     */
    public void drop(SlashCommandInteractionEvent event, CommandInfo ci) {
        int columnId = event.getOption("column").getAsInt();

        drop(Replyable.from(event), ci, columnId);
    }

    /**
     * Drops one mark to the board
     */
    public void drop(Replyable replyable, CommandInfo ci, int columnId) {
        long userId = ci.authorId();

        if (userId != id && userId != opponentId) {
            replyable.replyEphemeral("You are not part of this game!");
            return;
        }

        if ((id == userId) != isFirstPlayersTurn) {
            replyable.replyEphemeral("It's not your turn!");
            return;
        }

        if (board[columnId][5] != ' ') {
            replyable.reply("That column is already full!");
            return;
        }

        active(ci);
        int winState = dropMark(columnId);
        String dropMessage = "You dropped your mark on column " + (columnId + 1) + ".";
        if (winState == 0) {
            isFirstPlayersTurn = !isFirstPlayersTurn;
            replyable.reply(getEmbedWithField(dropMessage, "It's now <@" + getCurrentPlayerId() + ">'s turn."), getButtons());
            return;
        }

        String winnerMessage;
        if (winState == DRAW) {
            winnerMessage = "The board is full and the game ended on draw.";
        } else {
            winnerMessage = "<@" + getCurrentPlayerId() + "> won!";
        }

        MessageEmbed message = getEmbedWithField(dropMessage, winnerMessage);

        replyable.reply(message, getButtons());
        finish(replyable, ci, winState);
    }

    @Override
    protected EmbedBuilder getEmbedBase() {
        SelfUser selfUser = bot.getJda().getSelfUser();
        return new EmbedBuilder()
                .setTitle("CONNECT 4")
                .setColor(type.color)
                .addField("Board", toEmojis(), true)
                .addField("Players", ":red_circle: <@!" + id + ">\n:yellow_circle: <@!" + opponentId + ">", true)
                .setFooter(selfUser.getName(), selfUser.getAvatarUrl())
                .setTimestamp(new Date().toInstant());
    }

    /**
     * Returns an integer depending on who is the winner.
     *
     * @return 0 if game is not ended.
     * 1 if player 1 won.
     * 2 if player 2 won.
     * 3 if the game ended on draw.
     * @see DuelMinigame#UNFINISHED
     * @see DuelMinigame#FIRST_PLAYER_WON
     * @see DuelMinigame#SECOND_PLAYER_WON
     * @see DuelMinigame#DRAW
     */
    public int getWinState() {
        int winner = getWinnerOnHorizontalRows();
        if (winner != 0) return winner;

        winner = getWinnerOnVerticalRows();
        if (winner != 0) return winner;

        winner = getWinnerOnDiagonalTopLeft();
        if (winner != 0) return winner;

        winner = getWinnerOnDiagonalTopRight();
        if (winner != 0) return winner;

        return hasEmptySpots() ? UNFINISHED : DRAW;
    }

    /**
     * Looks for the winner on and only on horizontal rows.
     *
     * @return 1 if first player has won.
     * 2 if the second player has won.
     * 0 otherwise.
     */
    public int getWinnerOnHorizontalRows() {
        int checking;
        int streak;
        for (int y = 0; y < 6; y++) {
            checking = ' ';
            streak = 0;
            for (int x = 0; x < 7; x++) {
                char mark = board[x][y];

                if (mark == ' ') {
                    checking = ' ';
                    // No need to reset streak since it's reset when the mark is different from empty.
                    continue;
                }

                if (mark == checking) {
                    streak++;

                    if (streak == 4) {
                        return mark - 0x30; // '1' - 0x30 = 1
                    }
                    continue;
                }

                if (x >= 4) break; // No room for new streak of 4
                streak = 1;
                checking = mark;
            }
        }

        return UNFINISHED;
    }

    /**
     * Looks for the winner on and only on vertical rows.
     *
     * @return 1 if first player has won.
     * 2 if the second player has won.
     * 0 otherwise.
     */
    public int getWinnerOnVerticalRows() {
        int checking;
        int streak;

        for (char[] column : board) {
            checking = ' ';
            streak = 0;
            int y = -1;
            for (char mark : column) {
                y++;
                if (mark == ' ') {
                    checking = ' ';
                    // No need to reset streak since it's reset when the mark is different from empty.
                    continue;
                }

                if (mark == checking) {
                    streak++;

                    if (streak == 4) {
                        return mark - 0x30; // '1' - 0x30 = 1
                    }
                    continue;
                }

                if (y >= 3) break; // No room for new streak of 4
                checking = mark;
                streak = 1;
            }
        }

        return UNFINISHED;
    }

    /**
     * Looks for the winner on and only on diagonal rows where the top most mark is on left.
     *
     * @return 1 if first player has won.
     * 2 if the second player has won.
     * 0 otherwise.
     */
    public int getWinnerOnDiagonalTopLeft() {
        int checking;
        int streak;

        for (int xBase = 3; xBase < 9; xBase++) {
            checking = ' ';
            streak = 0;
            int x = xBase;
            int y = 0;

            while (y < 6 && x >= 0) {
                if (x >= 7) {
                    x--;
                    y++;
                    continue;
                    // Some diagonal rows don't have an entry at y=0, For example:

                    // O O X O O O O
                    // O O O X O O O
                    // O O O O X O O
                    // O O O O O X O
                    // O O O O O O X
                    // O O O O O O O
                }

                char mark = board[x][y];
                if (mark == ' ') {
                    checking = ' ';
                    // No need to reset streak since it's reset when the mark is different from empty.
                    x--;
                    y++;
                    continue;
                }

                if (mark == checking) {
                    streak++;

                    if (streak == 4) {
                        return mark - 0x30; // '1' - 0x30 = 1
                    }
                    x--;
                    y++;
                    continue;
                }

                if (y >= 3) break; // No room for new streak of 4
                checking = mark;
                streak = 1;

                x--;
                y++;
            }
        }

        return UNFINISHED;
    }

    /**
     * Looks for the winner on and only on diagonal rows where the top most mark is on right.
     *
     * @return 1 if first player has won.
     * 2 if the second player has won.
     * 0 otherwise.
     */
    public int getWinnerOnDiagonalTopRight() {
        int checking;
        int streak;

        for (int xBase = 3; xBase >= -2; xBase--) {
            checking = ' ';
            streak = 0;
            int x = xBase;
            int y = 0;

            while (y < 6 && x < 7) {
                if (x < 0) {
                    x++;
                    y++;
                    continue;
                }

                char mark = board[x][y];
                if (mark == ' ') {
                    checking = ' ';
                    // No need to reset streak since it's reset when the mark is different from empty.
                    x++;
                    y++;
                    continue;
                }

                if (mark == checking) {
                    streak++;

                    if (streak == 4) {
                        return mark - 0x30; // '1' - 0x30 = 1
                    }
                    x++;
                    y++;
                    continue;
                }

                if (y >= 3) break; // No room for new streak of 4
                checking = mark;
                streak = 1;

                x++;
                y++;
            }
        }

        return UNFINISHED;
    }

    /**
     * Checks if the board has empty spots.
     *
     * @return true if new marks can be inputted.
     */
    public boolean hasEmptySpots() {
        for (int x = 0; x < 7; x++) {
            if (board[x][5] == ' ') return true;
        }

        return false;
    }

    /**
     * Converts the board to 42 mark emojis and 7 number emojis giving each row its own id.
     *
     * @return Emotes and new lines as a String.
     */
    public String toEmojis() {
        StringBuilder row0 = new StringBuilder();
        StringBuilder row1 = new StringBuilder();
        StringBuilder row2 = new StringBuilder();
        StringBuilder row3 = new StringBuilder();
        StringBuilder row4 = new StringBuilder();
        StringBuilder row5 = new StringBuilder();

        for (char[] column : board) {
            row0.append(getEmojiName(column[0]));
            row1.append(getEmojiName(column[1]));
            row2.append(getEmojiName(column[2]));
            row3.append(getEmojiName(column[3]));
            row4.append(getEmojiName(column[4]));
            row5.append(getEmojiName(column[5]));
        }

        return row5.append('\n')
                .append(row4).append('\n')
                .append(row3).append('\n')
                .append(row2).append('\n')
                .append(row1).append('\n')
                .append(row0).append('\n')
                .append(":one::two::three::four::five::six::seven:").toString();
    }

    /**
     * Returns the name of the emoji corresponding the given mark.
     */
    public String getEmojiName(char mark) {
        if (mark == ' ') return ":black_circle:";
        if (mark == '1') return ":red_circle:";
        return ":yellow_circle:";
    }

    public int dropMark(int columnId) {
        char[] column = board[columnId];
        char mark = isFirstPlayersTurn ? '1' : '2';

        if (column[2] == ' ') {
            if (column[1] == ' ') {
                if (column[0] == ' ') {
                    column[0] = mark;
                } else {
                    column[1] = mark;
                }
            } else {
                column[2] = mark;
            }
        } else if (column[4] == ' ') {
            if (column[3] == ' ') {
                column[3] = mark;
            } else {
                column[4] = mark;
            }
        } else {
            column[5] = mark;
        }

        return getWinState();
    }

    /**
     * Returns the buttons for one game.
     */
    public ActionRow[] getButtons() {
        return new ActionRow[]{
                ActionRow.of(getButton(0), getButton(1), getButton(2), getButton(3)),
                ActionRow.of(getButton(4), getButton(5), getButton(6))
        };
    }

    /**
     * Returns the button representing the given column.
     */
    public Button getButton(int column) {
        String buttonId = "connect4-" + id + "-" + column;

        return Button.secondary(buttonId, Integer.toString(column + 1)).withDisabled(board[column][5] != ' ');
    }

    @Override
    public String quit(@Nullable Replyable replyable) {
        super.quit(replyable);
        return "You quit your previous game of connect 4. You didn't receive any rewards.";
    }
}
