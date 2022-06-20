package io.github.adex720.minigames.util.replyable;

import io.github.adex720.minigames.MinigamesBot;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A class serving as a way to use multiple types of Discord events
 * as one parameter while keeping replying to it easy.
 * Methods for getting a {@link MessageAction} or {@link WebhookMessageAction} are also supported.
 *
 * @author adex720
 */
public class Replyable {

    private final boolean isWebhookBased;
    private Message lastMessage;
    private boolean processingLastMessage;

    Replyable(boolean isWebhookBased) {
        this.isWebhookBased = isWebhookBased;
        lastMessage = null;
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
        if (message.isEmpty()) return;
        processingLastMessage = true;
        if (isWebhookBased) {
            getWebhookMessageAction(message).queue(this::updateLastMessage);
            return;
        }

        getMessageAction(message).queue(this::updateLastMessage);
    }

    /**
     * Sends the message to the channel.
     */
    public void reply(MessageEmbed message) {
        if (isWebhookBased) {
            getWebhookMessageAction(message).queue(this::updateLastMessage);
            return;
        }

        getMessageAction(message).queue(this::updateLastMessage);
    }

    /**
     * Sends the message to the channel.
     * The message will be ephemeral (Hidden to other users) if possible.
     */
    public void replyEphemeral(String message) {
        if (message.isEmpty()) return;
        processingLastMessage = true;
        if (isWebhookBased) {
            getWebhookMessageAction(message).setEphemeral(true).queue(this::updateLastMessage);
            return;
        }

        getMessageAction(message).queue(this::updateLastMessage);
    }

    /**
     * Sends the message to the channel.
     * The message will be ephemeral (Hidden to other users) if possible.
     */
    public void replyEphemeral(MessageEmbed message) {
        processingLastMessage = true;
        if (isWebhookBased) {
            getWebhookMessageAction(message).setEphemeral(true).queue(this::updateLastMessage);
            return;
        }

        getMessageAction(message).queue(this::updateLastMessage);
    }

    /**
     * Sends the message with the {@link ActionRow}s to the channel.
     */
    public void reply(String message, ActionRow... actionRows) {
        if (message.isEmpty()) return;
        processingLastMessage = true;
        if (!isWebhookBased) {
            getMessageAction(message).setActionRows(actionRows).queue(this::updateLastMessage);
            return;
        }

        getWebhookMessageAction(message).addActionRows(actionRows).queue(this::updateLastMessage);
    }

    /**
     * Sends the message with the {@link ActionRow}s to the channel.
     */
    public void reply(MessageEmbed message, ActionRow... actionRows) {
        processingLastMessage = true;
        if (!isWebhookBased) {
            getMessageAction(message).setActionRows(actionRows).queue(this::updateLastMessage);
            return;
        }

        getWebhookMessageAction(message).addActionRows(actionRows).queue(this::updateLastMessage);
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

    private void updateLastMessage(Message message) {
        lastMessage = message;
        processingLastMessage = false;
    }

    private void updateLastMessage(Object message) {
        lastMessage = (Message) message;
        processingLastMessage = false;
    }

    /**
     * Returns the last message which was sent by this replyable.
     * If no message is sent yet, null is returned.
     * The cached message doesn't get updated when the action is created with
     * {@link #getMessageAction(String)}, {@link #getMessageAction(MessageEmbed)},
     * {@link #getWebhookMessageAction(String)} or {@link #getWebhookMessageAction(MessageEmbed)}
     * and queued (or completed, etc.) outside this class.
     */
    @Nullable
    @CheckReturnValue
    public Message getLastMessage() {
        return lastMessage;
    }

    /**
     * Returns the last message which was sent by this replyable.
     * If there last message is not updated yet, this method will wait until it is sent.
     * If no message is sent yet or the updating of the last message takes over 5 seconds, null is returned.
     * <p>
     * The cached message doesn't get updated when the action is created with
     * {@link #getMessageAction(String)}, {@link #getMessageAction(MessageEmbed)},
     * {@link #getWebhookMessageAction(String)} or {@link #getWebhookMessageAction(MessageEmbed)}
     * and queued (or completed, etc.) outside this class.
     * This doesn't result on the message being null, but the last message queued inside this replyable to be used.
     */
    public synchronized Message waitLastMessage() {
        if (!processingLastMessage) return lastMessage;
        long end = System.currentTimeMillis() + 5000;

        while (processingLastMessage && end < System.currentTimeMillis()) {
            continue;
        }
        return lastMessage;
    }

    /**
     * Returns the last message which was sent by this replyable.
     * If there last message is not updated yet, this method will wait until it is sent.
     * If no message is sent yet or the updating of the last message takes over the given time limit, null is returned.
     * <p>
     * The cached message doesn't get updated when the action is created with
     * {@link #getMessageAction(String)}, {@link #getMessageAction(MessageEmbed)},
     * {@link #getWebhookMessageAction(String)} or {@link #getWebhookMessageAction(MessageEmbed)}
     * and queued (or completed, etc.) outside this class.
     * This doesn't result on the message being null, but the last message queued inside this replyable to be used.
     *
     * @param maxWait Amount of millis to wait for updating.
     */
    public Message waitLastMessage(int maxWait) {
        if (!processingLastMessage) return lastMessage;
        long end = System.currentTimeMillis() + maxWait;

        while (processingLastMessage && end < System.currentTimeMillis()) {
            continue;
        }
        return lastMessage;
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
        if (!event.isAcknowledged()) event.deferReply().queue();

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
                return event.getMessage().editMessage(message);
            }

            @Override
            public MessageAction getMessageAction(MessageEmbed message) {
                return event.getMessage().editMessageEmbeds(message);
            }
        };
    }

    /**
     * Creates a replyable which sends the messages as direct messages for the given user.
     * <p>
     * If opening a channel fails, {@link Replyable#IGNORE_ALL} is returned.
     */
    public static Replyable directMessage(User user) {
        RestAction<PrivateChannel> action = user.openPrivateChannel();
        AtomicReference<PrivateChannel> channelAtomicReference = new AtomicReference<>();
        AtomicReference<Boolean> isChannelLoaded = new AtomicReference<>(false);

        action.queue(channel -> {
            channelAtomicReference.set(channel);
            isChannelLoaded.set(true);
        });

        while (!isChannelLoaded.get()) {
        }

        return new Replyable(false) {
            @Override
            public MessageAction getMessageAction(String message) {
                return channelAtomicReference.get().sendMessage(message);
            }

            @Override
            public MessageAction getMessageAction(MessageEmbed message) {
                return channelAtomicReference.get().sendMessageEmbeds(message);
            }
        };
    }

    /**
     * Creates a replyable which sends the messages as direct messages for the given user.
     * <p>
     * If opening a channel fails, {@link Replyable#IGNORE_ALL} is returned.
     */
    public static Replyable directMessage(long userId, MinigamesBot bot) {
        User user = bot.getJda().getUserById(userId);
        if (user != null) return directMessage(user);
        return IGNORE_ALL;
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
