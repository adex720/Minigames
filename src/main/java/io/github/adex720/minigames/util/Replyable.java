package io.github.adex720.minigames.util;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageAction;

public class Replyable {

    private Replyable(){
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
        MessageAction messageAction =  getMessageAction(message);
        if (messageAction != null){
            messageAction.queue();
            return;
        }

        getWebhookMessageAction(message).queue();
    }

    public void reply(MessageEmbed message) {
        MessageAction messageAction =  getMessageAction(message);
        if (messageAction != null){
            messageAction.queue();
            return;
        }

        getWebhookMessageAction(message).queue();
    }

    public static Replyable from(MessageReceivedEvent event) {
        return new Replyable() {
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

    public static Replyable from(SlashCommandEvent event) {
        return new Replyable() {
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

}
