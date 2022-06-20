package io.github.adex720.minigames.util.replyable;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;

/**
 * A {@link Replyable} which doesn't send any messages to Discord.
 * however, it saves all messages and allows to compare them to test functionality.
 *
 * @author adex720
 */
public class ReplyableTest extends Replyable {

    private final ArrayList<ReplyableMessage> messages;

    public ReplyableTest() {
        super(false);
        messages = new ArrayList<>();
    }

    @Override
    public void reply(String message) {
        messages.add(new ReplyableMessage(message));
    }

    @Override
    public void reply(MessageEmbed message) {
        messages.add(new ReplyableMessage(message));
    }

    @Override
    public void replyEphemeral(String message) {
        reply(message);
    }

    @Override
    public void replyEphemeral(MessageEmbed message) {
        reply(message);
    }

    @Override
    public void reply(String message, ActionRow... actionRows) {
        messages.add(new ReplyableMessage(message, actionRowToButtons(actionRows)));
    }

    @Override
    public void reply(MessageEmbed message, ActionRow... actionRows) {
        messages.add(new ReplyableMessage(message, actionRowToButtons(actionRows)));
    }

    @Override
    public void reply(String message, Button... buttons) {
        messages.add(new ReplyableMessage(message, buttons));
    }

    @Override
    public void reply(MessageEmbed message, Button... buttons) {
        messages.add(new ReplyableMessage(message, buttons));
    }

    public Button[] actionRowToButtons(ActionRow[] actionRows) {
        if (actionRows.length == 0) return new Button[0];

        ArrayList<Button> buttons = new ArrayList<>();
        for (ActionRow actionRow : actionRows) {
            buttons.addAll(actionRow.getButtons());
        }

        return buttons.toArray(new Button[0]);
    }
}
