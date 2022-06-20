package io.github.adex720.minigames.util.replyable;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores a message which was sent by {@link ReplyableTest}.
 * The messages can be tested by test.
 *
 * @author adex720
 */
public class ReplyableMessage {

    private final boolean isEmbed;

    @Nullable
    private final String messageText;
    private final MessageEmbed messageEmbed;

    private final ArrayList<String> buttonIds;

    public ReplyableMessage(@Nonnull String message, Button... buttons) {
        isEmbed = false;

        messageText = message;
        messageEmbed = null;

        buttonIds = new ArrayList<>();
        for (Button button : buttons) {
            buttonIds.add(button.getId());
        }
    }

    public ReplyableMessage(@Nonnull MessageEmbed message, Button... buttons) {
        isEmbed = true;

        messageText = null;
        messageEmbed = message;

        buttonIds = new ArrayList<>();
        for (Button button : buttons) {
            buttonIds.add(button.getId());
        }
    }

    /**
     * Returns true if the message perfectly matches with the given values.
     *
     * @param expected The expected content of the message
     */
    public boolean doesContentMatch(String expected) {
        if (isEmbed) return false;

        return messageText.equals(expected);
    }

    /**
     * Returns true if the message perfectly matches with the given values.
     *
     * @param title          Title of the {@link MessageEmbed}
     * @param expectedFields Fields of the {@link MessageEmbed}
     */
    public boolean doesContentMatch(@Nullable String title, @Nonnull MessageEmbed.Field[] expectedFields) {
        if (!isEmbed) return false;

        if ((title == null) != (messageEmbed.getTitle() == null)) return false;

        if (title != null == !messageEmbed.getTitle().equals(title)) return false;

        List<MessageEmbed.Field> fields = messageEmbed.getFields();
        if (fields.size() != expectedFields.length) return false;

        for (int i = 0; i < expectedFields.length; i++) {
            if (!fields.get(i).equals(expectedFields[i])) return false;
        }

        return true;
    }

    /**
     * Returns true if the message is a {@link MessageEmbed}
     */
    public boolean isEmbed() {
        return isEmbed;
    }

    /**
     * Returns true if the message has no buttons.
     */
    public boolean hasButtons() {
        return !buttonIds.isEmpty();
    }

    /**
     * Returns true if the message has a button with tha
     */
    public boolean hasButton(String id) {
        return buttonIds.contains(id);
    }

    /**
     * Returns true if each of the given ids is represents on the ids of the buttons on the message.
     */
    public boolean hasButtons(String... ids) {
        for (String id : ids) {
            if (!buttonIds.contains(id)) return false;
        }

        return true;
    }

    /**
     * Returns true if the message has a buttons with each of the given ids, on the correct order, and no extra buttons.
     */
    public boolean doesButtonsMatch(String[] ids) {
        if (buttonIds.size() != ids.length) return false;

        for (int i = 0; i < ids.length; i++) {
            if (!ids[i].equals(buttonIds.get(i))) return false;
        }

        return true;
    }
}
