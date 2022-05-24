package io.github.adex720.minigames.util.replyable;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

import javax.annotation.CheckReturnValue;

/**
 * A class serving as a way to use multiple types of Discord events
 * as one parameter while keeping replying to it easy.
 * Methods for getting a {@link MessageAction} or {@link WebhookMessageAction} are also supported.
 *
 * @author adex720
 */
public class Replyable {

    private final boolean isWebhookBased;

    Replyable(boolean isWebhookBased) {
        this.isWebhookBased = isWebhookBased;
    }

    /**
     * Returns a {@link MessageAction} which sends the given message.
     */
    @CheckReturnValue
    public MessageAction getMessageAction(String message) {
        return null;
    }

    /**
     * Returns a {@link MessageAction} which sends the given message.
     */
    @CheckReturnValue
    public MessageAction getMessageAction(MessageEmbed message) {
        return null;
    }

    /**
     * Returns a {@link WebhookMessageAction} which sends the given message.
     */
    @CheckReturnValue
    public WebhookMessageAction<?> getWebhookMessageAction(String message) {
        return null;
    }

    /**
     * Returns a {@link WebhookMessageAction} which sends the given message.
     */
    @CheckReturnValue
    public WebhookMessageAction<?> getWebhookMessageAction(MessageEmbed message) {
        return null;
    }

    /**
     * Sends the message to the channel.
     */
    public void reply(String message) {
        if (isWebhookBased) {
            getWebhookMessageAction(message).queue();
            return;
        }

        getMessageAction(message).queue();
    }

    /**
     * Sends the message to the channel.
     */
    public void reply(MessageEmbed message) {
        if (isWebhookBased) {
            getWebhookMessageAction(message).queue();
            return;
        }

        getMessageAction(message).queue();
    }

    /**
     * Sends the message to the channel.
     * The message will be ephemeral (Hidden to other users) if possible.
     */
    public void replyEphemeral(String message) {
        if (isWebhookBased) {
            getWebhookMessageAction(message).setEphemeral(true).queue();
            return;
        }

        getMessageAction(message).queue();
    }

    /**
     * Sends the message to the channel.
     * The message will be ephemeral (Hidden to other users) if possible.
     */
    public void replyEphemeral(MessageEmbed message) {
        if (isWebhookBased) {
            getWebhookMessageAction(message).setEphemeral(true).queue();
            return;
        }

        getMessageAction(message).queue();
    }

    /**
     * Sends the message with the {@link ActionRow}s to the channel.
     */
    public void reply(String message, ActionRow... actionRows) {
        if (!isWebhookBased) {
            getMessageAction(message).setActionRows(actionRows).queue();
            return;
        }

        getWebhookMessageAction(message).addActionRows(actionRows).queue();
    }

    /**
     * Sends the message with the {@link ActionRow}s to the channel.
     */
    public void reply(MessageEmbed message, ActionRow... actionRows) {
        if (!isWebhookBased) {
            getMessageAction(message).setActionRows(actionRows).queue();
            return;
        }

        getWebhookMessageAction(message).addActionRows(actionRows).queue(m-> System.out.println("Sent"));
    }

    /**
     * Sends the given message with the {@link Button}s to the channel.
     * If more than 5 buttons exist, {@link Replyable#reply(String, ActionRow...)} should be used instead.
     */
    public void reply(String message, Button... buttons) {
        reply(message, ActionRow.of(buttons));
    }

    /**
     * Sends the given message with the {@link Button}s to the channel.
     * If more than 5 buttons exist, {@link Replyable#reply(MessageEmbed, ActionRow...)} should be used instead.
     */
    public void reply(MessageEmbed message, Button... buttons) {
        reply(message, ActionRow.of(buttons));
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

    /**
     * Creates a replyable for {@link MessageReceivedEvent}.
     * Replying sends a new message to the channel.
     */
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

    /**
     * Creates a replyable for {@link SlashCommandInteractionEvent}.
     * Replying sends a new message to the channel.
     */
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

    /**
     * Creates a replyable for {@link ButtonInteractionEvent}.
     * Replying sends a new message to the channel.
     */
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


    /**
     * Creates a replyable for {@link ButtonInteractionEvent}.
     * Replying edits the message whose button was pressed.
     */
    public static Replyable edit(ButtonInteractionEvent event) {
        return new Replyable(false) {
            @Override
            public MessageAction getMessageAction(String message) {
                event.deferEdit().queue();
                return event.getMessage().editMessage(message).setActionRow();
            }

            @Override
            public MessageAction getMessageAction(MessageEmbed message) {
                event.deferEdit().queue();
                return event.getMessage().editMessageEmbeds(message);
            }
        };
    }

    /**
     * This Replyable doesn't send any messages.
     * Trying to get any action return null.
     */
    public static final Replyable IGNORE_ALL = new Replyable(false) {
        @Override
        public void reply(String message, ActionRow... actionRows) {
        }

        @Override
        public void reply(MessageEmbed message, ActionRow... actionRows) {
        }
    };

}
