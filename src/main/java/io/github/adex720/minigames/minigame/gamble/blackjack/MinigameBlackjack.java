package io.github.adex720.minigames.minigame.gamble.blackjack;

import com.google.gson.JsonObject;
import io.github.adex720.minigames.MinigamesBot;
import io.github.adex720.minigames.discord.command.CommandInfo;
import io.github.adex720.minigames.gameplay.profile.Profile;
import io.github.adex720.minigames.minigame.gamble.GambleMinigame;
import io.github.adex720.minigames.util.JsonHelper;
import io.github.adex720.minigames.util.Replyable;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.internal.interactions.component.ButtonImpl;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * @author adex720
 */
public class MinigameBlackjack extends GambleMinigame {

    public static final String[] CARD_NUMBER_NAMES = new String[]
            {"Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King"};

    public static final String[] SUIT_NAMES = new String[]{"Club", "Spade", "Diamond", "Heart"};

    private final ArrayList<Integer> dealerCards;
    private final ArrayList<Integer> playerCards;
    private int handSum;

    public MinigameBlackjack(MinigamesBot bot, @Nullable Profile profile, long id, long lastActive, int bet) {
        super(bot, bot.getMinigameTypeManager().BLACKJACK, profile, id, lastActive, bet);

        this.dealerCards = new ArrayList<>();
        this.playerCards = new ArrayList<>();
    }

    public MinigameBlackjack(MinigamesBot bot, long id, long lastActive, int bet, float betMultiplier, int[] dealerCardsArray, int[] playerCardsArray) {
        this(bot, null, id, lastActive, bet);
        this.betMultiplier = betMultiplier;

        for (int card : dealerCardsArray) {
            this.dealerCards.add(card);
        }

        for (int card : playerCardsArray) {
            this.playerCards.add(card);
        }
    }

    public MinigameBlackjack(CommandInfo ci, int bet) {
        this(ci.bot(), ci.profile(), ci.gameId(), System.currentTimeMillis(), bet);
    }

    /**
     * It should be checked if the player can afford the bet before this is called.
     */
    public static MinigameBlackjack start(SlashCommandInteractionEvent event, CommandInfo commandInfo, int bet) {
        MinigameBlackjack minigame = new MinigameBlackjack(commandInfo, bet);

        if (minigame.dealStartCards(Replyable.from(event), commandInfo)) {
            event.getHook().sendMessage("You started a new game of blackjack!\n" + minigame)
                    .addActionRows(minigame.getActionRow()).queue();
        } else {
            event.getHook().sendMessage("You started a new game of blackjack! You got a blackjack and won!\n"
                    + minigame).queue();
        }

        return minigame;
    }

    /**
     * It should be checked if the player can afford the bet before this is called.
     */
    public static MinigameBlackjack start(ButtonInteractionEvent event, CommandInfo commandInfo, int bet) {
        MinigameBlackjack minigame = new MinigameBlackjack(commandInfo, bet);

        if (minigame.dealStartCards(Replyable.from(event), commandInfo)) {
            event.getHook().sendMessage("You started a new game of blackjack!\n" + minigame)
                    .addActionRows(minigame.getActionRow()).queue();
            return minigame;
        }

        event.getHook().sendMessage("You started a new game of blackjack! You got a blackjack and won!\n"
                + minigame).queue();

        return null;
    }

    public static MinigameBlackjack fromJson(JsonObject json, MinigamesBot bot) {
        long id = JsonHelper.getLong(json, "id");
        long lastActive = JsonHelper.getLong(json, "active");

        int bet = JsonHelper.getInt(json, "bet");
        float betMultiplier = JsonHelper.getFloat(json, "bet-multi");

        int[] dealerCards = JsonHelper.jsonArrayToIntArray(JsonHelper.getJsonArray(json, "dealer"));
        int[] playerCards = JsonHelper.jsonArrayToIntArray(JsonHelper.getJsonArray(json, "player"));

        return new MinigameBlackjack(bot, id, lastActive, bet, betMultiplier, dealerCards, playerCards);
    }

    @Override
    public JsonObject getAsJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", "blackjack");

        json.addProperty("id", id);
        json.addProperty("active", lastActive);

        json.addProperty("bet", bet);
        json.addProperty("bet-multi", betMultiplier);

        json.add("dealer", JsonHelper.arrayListIntToJsonArray(dealerCards));
        json.add("player", JsonHelper.arrayListIntToJsonArray(playerCards));

        return json;
    }

    /**
     * @return true if the game didn't end
     */
    private boolean dealStartCards(Replyable replyable, CommandInfo commandInfo) {
        drawCardForPlayerWithMessage();
        drawCardForPlayerWithMessage();

        dealerCards.add(getNextCard());

        if (handSum == 21) { // blackjack
            // Reply is handled at MinigameBlackjack#start()
            betMultiplier *= 1.5f;
            finish(replyable, commandInfo, true);
            return false;
        }

        return true;
    }

    /**
     * Draws a card for the player.
     * The game ends if the sum of the cards goes over 21.
     */
    public void hit(Replyable replyable, CommandInfo commandInfo) {
        active(commandInfo);
        String cardMessage = drawCardForPlayerWithMessage();

        if (checkForWin(cardMessage, replyable, commandInfo)) {
            return;
        }

        replyable.reply(cardMessage, getActionRow());
    }

    /**
     * 1. Doubles the bet for winning.<p>
     * 2. Draws one card for the player.<p>
     * 3. Draws cards for dealer.<p>
     * 4. Calculates winner.
     */
    public void hitDouble(Replyable replyable, CommandInfo commandInfo) {
        active(commandInfo);
        betMultiplier *= 2;

        int cardId = drawCardForPlayer();
        String message = "You doubled your bet and drew a " + getCardName(cardId) + "! Now it's the dealers turn.";

        if (handSum > 21) {
            replyable.reply(message + " You busted and lost your bet!");
            finish(replyable, commandInfo, false);
            return;
        }

        stand(replyable, commandInfo);
    }

    /**
     * @param message     Text before win state.
     *                    This is only sent if the game ended.
     * @param replyable   Replyable
     * @param commandInfo Command info
     * @return True if the game ended.
     */
    public boolean checkForWin(String message, Replyable replyable, CommandInfo commandInfo) {
        if (handSum > 21) {
            replyable.reply(message + " You busted and lost your bet!");
            finish(replyable, commandInfo, false);
            return true;
        }

        if (playerCards.size() == 5) {
            replyable.reply(message + ". You got 5 cards on your hand and won!");
            finish(replyable, commandInfo, true);
            return true;
        }

        return false;
    }

    /**
     * Draws cards for the dealer and calculates the winner.
     */
    public void stand(Replyable replyable, CommandInfo commandInfo) {
        active(commandInfo);
        int dealerCount = drawCardsForDealer();
        String cards = toString();

        if (dealerCount > 21) {
            replyable.reply(cards + "\nThe dealer busted and you won!");
            finish(replyable, commandInfo, true);
            return;
        }

        if (dealerCount > handSum) {
            replyable.reply(cards + "\nThe dealer won and you lost your bet!");
            return;
        }

        if (dealerCount < handSum) {
            replyable.reply(cards);
            finish(replyable, commandInfo, true);
            return;
        }

        replyable.reply(cards + "\nThe game ended on a draw and you got your bet back.");
        Profile profile = commandInfo.profile();
        appendQuest(replyable, profile, false);
        appendStats(profile, false);
        delete(replyable);
    }

    /**
     * Draws a new card for the player.
     *
     * @return Message containing id of the card and new sum of cards.
     */
    public String drawCardForPlayerWithMessage() {
        int cardId = drawCardForPlayer();

        return "You drew a " + getCardName(cardId) + " and the sum of your cards is " + handSum + ".";
    }

    /**
     * Draws a new card for the player.
     *
     * @return id of the card.
     */
    public int drawCardForPlayer() {
        int cardId = getNextCard();
        playerCards.add(cardId);
        increaseHandSum(cardId);
        return cardId;
    }

    /**
     * @return an {@link ActionRow} containing the correct buttons.
     */
    public ActionRow getActionRow() {
        if (handSum == 21) return ActionRow.of(Buttons.hitDisabled(), Buttons.stand(id));
        if (playerCards.size() == 2) return ActionRow.of(Buttons.hit(id), Buttons.stand(id), Buttons.doubleBet(id));
        return ActionRow.of(Buttons.hit(id), Buttons.stand(id));
    }

    /**
     * The bank contains infinite amount of decks,
     * and therefore each card is as like as others,
     * even when few cards are already drawn.
     *
     * @return a number between 0 and 51 (4 * 13 - 1 = 51)
     */
    public int getNextCard() {
        return bot.getRandom().nextInt(52);
    }

    /**
     * Increases {@link MinigameBlackjack#handSum} with the amount of points the card gives.
     *
     * @param cardId id of the card.
     */
    public void increaseHandSum(int cardId) {
        int cardNumber = getCardNumber(cardId);

        // number cards
        if (cardNumber >= 2 && cardNumber <= 10) {
            handSum += cardNumber;
            return;
        }

        // court cards
        if (cardNumber > 10) {
            handSum += 10;
            return;
        }

        // ace is 1 or 11 depending on which is better
        if (handSum > 10) {
            handSum++;
            return;
        }

        handSum += 11;
    }

    /**
     * Transforms the given card id to the English name of the card.
     * Example names are 'Spade 7', 'Diamond Ace' and 'Club Queen'.
     */
    public static String getCardName(int cardId) {
        return SUIT_NAMES[getSuitId(cardId)] + " " + CARD_NUMBER_NAMES[getCardNumberId(cardId)];
    }

    /**
     * Returns the number the card is assigned with.
     * Ace is 1, Jack is 11, Queen is 12 and King is 13.
     */
    public static int getCardNumber(int cardId) {
        return cardId % 13 + 1;
    }

    /**
     * Returns the id of the card number.
     * The ids are on the same order as the cards
     * starting from 0 which is Ace and ending at 12 which is King.
     *
     * @see MinigameBlackjack#CARD_NUMBER_NAMES
     */
    public static int getCardNumberId(int cardId) {
        return cardId % 13;
    }

    /**
     * Returns the id of the suit of the card.
     * The ids are:
     * <ul>
     *     <li> 0 - Club
     *     <li> 1 - Spade
     *     <li> 2 - Diamond
     *     <li> 3 - Heart
     * </ul>
     *
     * @see MinigameBlackjack#SUIT_NAMES
     */
    public static int getSuitId(int cardId) {
        return cardId / 13;
    }

    /**
     * Returns the amount of points the card counts as if the sum of the hand was 1.
     */
    public static int getCardDefaultNumber(int cardId) {
        int numberId = getCardNumberId(cardId);

        if (numberId >= 9) return 10;
        if (numberId >= 1) return numberId + 1;
        return 11;
    }

    /**
     * Assumes that only one card is drawn.
     *
     * @return the sum of dealers cards once the sum is above or 17.
     */
    public int drawCardsForDealer() {
        int count = getCardDefaultNumber(dealerCards.get(0));

        while (count < 17) {
            int cardId = getNextCard();
            dealerCards.add(cardId);

            int cardNumber = getCardNumber(cardId);

            if (cardNumber >= 10) count += 10;
            else if (cardNumber >= 2) count += cardNumber;
            else {
                if (count < 11) count += 11;
                else count += 1;
            }
        }

        return count;
    }

    /**
     * Returns the sum of the cards the dealer has.
     */
    public int getDealerHandSum() {
        int count = 0;
        for (int cardId : dealerCards) {
            int cardNumber = getCardNumber(cardId);

            if (cardNumber >= 10) count += 10;
            else if (cardNumber >= 2) count += cardNumber;
            else {
                if (count < 11) count += 11;
                else count += 1;
            }
        }

        return count;
    }

    /**
     * @return true if a card can be hit.
     */
    public boolean canHit() {
        return handSum < 21;
    }

    /**
     * @return true if only starting cards are dealt.
     */
    public boolean canDouble() {
        return playerCards.size() <= 2;
    }

    /**
     * Creates a String containing all dealt cards and their sums.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("Dealer cards: ");
        if (dealerCards.size() == 1) {
            int cardId = dealerCards.get(0);
            stringBuilder.append(getCardName(cardId))
                    .append(", flipped card. Total: ")
                    .append(getCardDefaultNumber(cardId));

        } else {
            boolean comma = false;

            for (int cardId : dealerCards) {
                if (comma) {
                    stringBuilder.append(", ");
                }
                comma = true;

                stringBuilder.append(getCardName(cardId));
            }

            stringBuilder.append(". Total: ")
                    .append(getDealerHandSum());
        }

        stringBuilder.append("\nYour cards: ");
        boolean comma = false;

        for (int cardId : playerCards) {
            if (comma) {
                stringBuilder.append(", ");
            }
            comma = true;

            stringBuilder.append(getCardName(cardId));
        }

        stringBuilder.append(" total: ").append(handSum);

        return stringBuilder.toString();
    }

    @Override
    public String quit(@Nullable Replyable replyable) {
        super.quit(replyable);
        return "You quit your previous game of blackjack. You lost your bet.";
    }

    @Override
    public String getReplayButtonId() {
        return super.getReplayButtonId() + "-" + bet;
    }

    /**
     * Contains methods for crating all required action buttons with given id.
     */
    public static class Buttons {

        public static Button hit(long id) {
            return new ButtonImpl("blackjack-hit-" + id, "HIT", ButtonStyle.SECONDARY, false, null); //TODO: add emotes
        }

        public static Button hitDisabled() {
            return new ButtonImpl("blackjack-hit-disabled", "HIT", ButtonStyle.SECONDARY, true, null);
        }

        public static Button stand(long id) {
            return new ButtonImpl("blackjack-stand-" + id, "STAND", ButtonStyle.SECONDARY, false, null);
        }

        public static Button doubleBet(long id) {
            return new ButtonImpl("blackjack-double-" + id, "DOUBLE", ButtonStyle.SECONDARY, false, null);
        }
    }
}
