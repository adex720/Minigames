package io.github.adex720.minigames.util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

/**
 * A class serving as a way to use multiple types of Discord events
 * as one parameter while keeping replying to it easy.
 * Methods for getting a {@link MessageAction} or {@link WebhookMessageAction} are also supported.
 *
 * @author adex720
 */
public class Replyable {

    private final boolean isWebhookBased;

    private Replyable(boolean isWebhookBased) {
        this.isWebhookBased = isWebhookBased;
    }

    public MessageAction getMessageAction(String message) {
        return null;
    }

    public MessageAction getMessageAction(MessageEmbed message) {
        return null;
    }

    public WebhookMessageAction<?> getWebhookMessageAction(String message) {
        return null;
    }

    public WebhookMessageAction<?> getWebhookMessageAction(MessageEmbed message) {
        return null;
    }

    public void reply(String message) {
        MessageAction messageAction = getMessageAction(message);
        if (messageAction != null) {
            messageAction.queue();
            return;
        }

        getWebhookMessageAction(message).queue();
    }

    public void reply(MessageEmbed message) {
        MessageAction messageAction = getMessageAction(message);
        if (messageAction != null) {
            messageAction.queue();
            return;
        }

        getWebhookMessageAction(message).queue();
    }

    /**
     * The ActionRows can only be added to the MessageAction if this returns true.
     *
     * @return true if the event is SlashCommand, Button, etc.
     * Returns false if the event is MessageReceived, etc.
     */
    public boolean isWebhookBased() {
        return isWebhookBased;
    }

    public static Replyable from(MessageReceivedEvent event) {
        return new Replyable(false) {
            @Override
            public MessageAction getMessageAction(String message) {
                return event.getChannel().sendMessage(message);
            }

            @Override
            public MessageAction getMessageAction(MessageEmbed message) {
                return event.getChannel().sendMessageEmbeds(message);
            }
        };
    }

    public static Replyable from(SlashCommandInteractionEvent event) {
        return new Replyable(true) {
            @Override
            public WebhookMessageAction<?> getWebhookMessageAction(String message) {
                return event.getHook().sendMessage(message);
            }

            @Override
            public WebhookMessageAction<?> getWebhookMessageAction(MessageEmbed message) {
                return event.getHook().sendMessageEmbeds(message);
            }
        };
    }

    public static Replyable from(ButtonInteractionEvent event) {
        return new Replyable(true) {
            @Override
            public WebhookMessageAction<?> getWebhookMessageAction(String message) {
                return event.getHook().sendMessage(message);
            }

            @Override
            public WebhookMessageAction<?> getWebhookMessageAction(MessageEmbed message) {
                return event.getHook().sendMessageEmbeds(message);
            }
        };
    }

    public static final Replyable IGNORE_ALL = new Replyable(false); // Doesn't send any messages. Trying to get the actions return null.

}
