package io.github.adex720.minigames.minigame.tictactoe;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.manager.minigame.MinigameTypeManager;
import io.github.adex720.minigames.minigame.duel.DuelMinigame;
import io.github.adex720.minigames.util.Util;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.util.Date;

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
        super(bot, typeManager.TIC_TAC_TOE, id, bot.getJda().getSelfUser().getIdLong(), false, lastActive);

        board = new char[3][3];
        fillBoard();

        if (!isFirstPlayersTurn) {
            makeAIMove();
        }
    }

    public MinigameTicTacToe(MinigamesBot bot, MinigameTypeManager typeManager, long id, long opponentId, long lastActive) {
        super(bot, typeManager.TIC_TAC_TOE, id, opponentId, true, lastActive);

        board = new char[3][3];
        fillBoard();
    }

    public static MinigameTicTacToe start(SlashCommandEvent event, CommandInfo ci) {
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

    public static MinigameTicTacToe start(ButtonClickEvent event, CommandInfo ci) {
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

    private void fillBoard() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                board[x][y] = ' ';
            }
        }
    }

    public static int compact(int x, int y) {
        return (x << 2) | y;
    }

    public static int getXFromCompacted(int compacted) {
        return (compacted >> 2) & 0x3;
    }

    public static String getPositionName(int position) {
        return getPositionName(getXFromCompacted(position), getYFromCompacted(position));
    }

    public static String getPositionName(int x, int y) {
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

    public static int getYFromCompacted(int compacted) {
        return compacted & 0x3;
    }

    public static String getColumnName(int x) {
        return switch (x) {
            case 0 -> "left";
            case 1 -> "middle";
            case 2 -> "right";
            default -> throw new IllegalStateException("Unexpected column: " + x);
        };
    }

    public static String getRowName(int y) {
        return switch (y) {
            case 0 -> "top";
            case 1 -> "middle";
            case 2 -> "bottom";
            default -> throw new IllegalStateException("Unexpected row: " + y);
        };
    }

    public void set(SlashCommandEvent event, CommandInfo ci) {
        long setter = ci.authorId();

        if (setter == id) {
            if (!isFirstPlayersTurn) {
                event.getHook().sendMessage("It is not your turn!").queue();
                return;
            }
        } else if (setter == opponentId) {
            if (isFirstPlayersTurn) {
                event.getHook().sendMessage("It is not your turn!").queue();
                return;
            }
        } else {
            event.getHook().sendMessage("You are not part of this game.").queue();
            return;
        }

        int x = (int) event.getOption("column").getAsLong();
        int y = (int) event.getOption("row").getAsLong();

        if (!isFree(x, y)) {
            event.getHook().sendMessageEmbeds(getEmbedWithField("Can't set mark", "That position is already in use.")).queue();
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
            event.getHook().sendMessageEmbeds(getEmbedWithField("Set your mark",
                    "You set your mark in " + getPositionName(x, y) + "."
                            + (isParty ? "" : "\nAI set its mark on " + lastAIMove))).queue();
            return;
        }

        if (winner == 3) {
            event.getHook().sendMessageEmbeds(getEmbedWithField("The game ended in a draw", "The board is full yet neither of the players has won.")).queue();
            finish(event, winner);
            return;
        }

        String winnerMention = "<@!" + (winner == FIRST_PLAYER_WON ? id : opponentId) + ">";
        event.getHook().sendMessageEmbeds(getEmbedWithField("The game ended", winnerMention + " won the game!")).queue();
        finish(event, winner);
    }

    public void place(int x, int y, boolean firstPlayer) {
        board[x][y] = firstPlayer ? 'x' : 'o';
    }

    /**
     * @return 0 if there's no winner,
     * 1 if first player is the winner,
     * 2 if second player is the winner and
     * 3 if te game is draw.
     */
    public int getWinner() {
        for (int line = ROW_TOP; line <= DIAGONAL_RIGHT_TOP; line++) {
            int score = getLineScore(line);
            if (score == 3) return FIRST_PLAYER_WON;
            if (score == -3) return SECOND_PLAYER_WON;
        }

        return doesBoardHaveEmptySpots() ? 0 : DRAW;
    }

    public boolean isFree(int x, int y) {
        return board[x][y] == ' ';
    }

    public boolean doesBoardHaveEmptySpots() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (board[x][y] == ' ') return true;
            }
        }

        return false;
    }

    @Override
    public EmbedBuilder getEmbedBase() {
        SelfUser selfUser = bot.getJda().getSelfUser();
        return new EmbedBuilder()
                .setTitle("TIC TAC TOE")
                .setColor(Util.MINIGAMES_COLOR)
                .addField("Board", getBoard(), true)
                .addField("Players", "**X** <@!" + id + ">\n**O** <@!" + opponentId + ">", true)
                .setFooter(selfUser.getName(), selfUser.getAvatarUrl())
                .setTimestamp(new Date().toInstant());
    }

    public String getBoard() {
        return "|" + getMark(0, 0) + "|" + getMark(1, 0) + "|" + getMark(2, 0) + "|\n" +
                "|" + getMark(0, 1) + "|" + getMark(1, 1) + "|" + getMark(2, 1) + "|\n" +
                "|" + getMark(0, 2) + "|" + getMark(1, 2) + "|" + getMark(2, 2) + "|";
    }

    public String getMark(int x, int y) {
        char mark = board[x][y];
        if (mark == ' ') return " ";
        if (mark == 'x') return "X";
        return "O";
    }

    /**
     * @param line the line to check.
     * @return the amount of marks the first player has on the row with the amount of marks of the second players reduced from it.
     */
    public int getLineScore(int line) {
        int score = 0;
        for (char mark : getLine(line)) {
            if (mark == 'x') score++;
            else if (mark == 'o') score--;
        }
        return score;
    }

    public int getFirstFreeOnLine(int line) {
        if (board[0][0] == ' ') {
            if (line == ROW_TOP || line == COLUMN_LEFT || line == DIAGONAL_LEFT_TOP) {
                return compact(0, 0);
            }
        }
        if (board[1][0] == ' ') {
            if (line == ROW_TOP || line == COLUMN_MIDDLE) {
                return compact(1, 0);
            }

        }
        if (board[2][0] == ' ') {
            if (line == ROW_TOP || line == COLUMN_RIGHT || line == DIAGONAL_RIGHT_TOP) {
                return compact(2, 0);
            }

        }
        if (board[0][1] == ' ') {
            if (line == ROW_MIDDLE || line == COLUMN_LEFT) {
                return compact(0, 1);
            }

        }
        if (board[1][1] == ' ') {
            if (line == ROW_MIDDLE || line == COLUMN_MIDDLE || line == DIAGONAL_LEFT_TOP || line == DIAGONAL_RIGHT_TOP) {
                return compact(1, 1);
            }

        }
        if (board[2][1] == ' ') {
            if (line == ROW_MIDDLE || line == COLUMN_RIGHT) {
                return compact(2, 1);
            }

        }
        if (board[0][2] == ' ') {
            if (line == ROW_BOTTOM || line == COLUMN_LEFT || line == DIAGONAL_RIGHT_TOP) {
                return compact(0, 2);
            }

        }
        if (board[1][2] == ' ') {
            if (line == ROW_BOTTOM || line == COLUMN_MIDDLE) {
                return compact(1, 2);
            }

        }
        if (board[2][2] == ' ') {
            if (line == ROW_BOTTOM || line == COLUMN_RIGHT || line == DIAGONAL_LEFT_TOP) {
                return compact(2, 2);
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
        return null;
    }

    public static MinigameTicTacToe fromJson(JsonObject json) {
        return null;
    }
}