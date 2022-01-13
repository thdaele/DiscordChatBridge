package net.dragonbabyfly.discordchatbridge;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.*;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;

public class DiscordBot extends ListenerAdapter {
    private final JDA jda;
    private final PlayerManager playerManager;
    private final static Long ChannelID = DiscordChatBridge.CONFIG.getValue("DiscordBot.chatChannelID", Long.class);
    private final static Long serverID = DiscordChatBridge.CONFIG.getValue("DiscordBot.serverID", Long.class);
    private final static String channelURL = String.format("https://discordapp.com/channels/%d/%d/", serverID, ChannelID);
    
    public DiscordBot(String token, PlayerManager playerManager) throws LoginException, InterruptedException {
        this.playerManager = playerManager;
        this.jda = JDABuilder
                .createDefault(token)
                .addEventListeners(this)
                .build()
                .awaitReady();
        this.sendToDiscord("Server started!");
    }

    public void sendToDiscord(String message) {
        final TextChannel channel = this.jda.getTextChannelById(DiscordBot.ChannelID);
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMember() == null || event.getAuthor().isBot() || event.getGuild().getIdLong() != serverID) {
            return;
        }

        final MessageChannel channel = event.getChannel();
        final Member member = event.getMember();
        final Message message = event.getMessage();
        final String content = message.getContentDisplay();
        final List<Message.Attachment> attachments = message.getAttachments();
        if (channel.getIdLong() == ChannelID) {
            final String name = event.getMember().getEffectiveName();
            final BaseText text = new LiteralText("\u24B9");
            final HoverEvent hoverText = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(member.getUser().getAsTag()));
            final ClickEvent clickText = new ClickEvent(ClickEvent.Action.OPEN_URL, channelURL);
//            text.fillStyle(new Style(TextColor.parse("blue"), null, null, null, null, null, null, null, null, null)).setColor(TextFormatting.BLUE).setHoverEvent(hoverText).setClickEvent(clickText);
            text.append(new TranslatableText("chat.type.text", name, content));
            for (Message.Attachment file : attachments) {
                final BaseText fileName = new LiteralText(" " + file.getFileName());
//                fileName.getStyle()
//                        .setColor(TextFormatting.BLUE)
//                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Open attachment")))
//                        .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, file.getUrl()));
                text.append(fileName);
            }
            this.playerManager.broadcast(text, MessageType.CHAT, Util.NIL_UUID);
        }
    }
}
