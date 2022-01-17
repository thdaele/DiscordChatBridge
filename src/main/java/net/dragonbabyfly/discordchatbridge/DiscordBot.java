package net.dragonbabyfly.discordchatbridge;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class DiscordBot extends ListenerAdapter {
    private final JDA jda;
    private final MinecraftServer server;
    private final static Long ChannelID = DiscordChatBridge.CONFIG.getValue("DiscordBot.chatChannelID", Long.class);
    private final static Long serverID = DiscordChatBridge.CONFIG.getValue("DiscordBot.serverID", Long.class);
    private final static String channelURL = String.format("https://discordapp.com/channels/%d/%d/", serverID, ChannelID);

    private final static String authorName = DiscordChatBridge.CONFIG.getValue("DiscordBot.botName", String.class);
    private final static String authorIcon = DiscordChatBridge.CONFIG.getValue("DiscordBot.botIconUrl", String.class);
    private final static Integer embedColor = DiscordChatBridge.CONFIG.getValue("DiscordBot.embedColor", Integer.class);

    public static final LocalDateTime BOT_START_TIME = LocalDateTime.now();
    
    public DiscordBot(String token, MinecraftServer minecraftServer) throws LoginException, InterruptedException {
        this.server = minecraftServer;
        this.jda = JDABuilder
                .createDefault(token)
                .addEventListeners(this)
                .build()
                .awaitReady();
        this.sendToDiscord("Server has started!");

        Guild guild = this.jda.getGuildById(serverID);
        TextChannel channel = this.jda.getTextChannelById(ChannelID);
        if (guild == null) {
            throw new LoginException("Invalid serverID inside config file");
        } else if (channel == null) {
            throw new LoginException("Invalid chatChannelID inside config file");
        }
        guild.upsertCommand("tps", "Shows server TPS and MSPT").queue();
        guild.upsertCommand("online", "Shows the online players").queue();
        guild.upsertCommand("uptime", "Shows the server uptime").queue();

        String activity = DiscordChatBridge.CONFIG.getValue("DiscordBot.status", String.class);
        this.jda.getPresence().setActivity(Activity.playing(activity));
    }

    public void shutDownBot() {
        this.jda.shutdown();
    }

    public String getUpTime() {
        LocalDateTime endTime = LocalDateTime.now();
        Duration botUpTime = Duration.between(BOT_START_TIME, endTime);
        // Didn't find any way to format it nicely so this will do fine
        String upTime = "Uptime: ";
        long days = botUpTime.toDaysPart();
        long hours = botUpTime.toHoursPart();
        long minutes = botUpTime.toMinutesPart();
        long seconds = botUpTime.toSecondsPart();
        if (days > 0) {
            upTime += String.format("%s day%s ", days, days > 1 ? "s" : "");
        }
        if (hours > 0) {
            upTime += String.format("%s hour%s ", hours, hours > 1 ? "s" : "");
        }
        if (minutes > 0) {
            upTime += String.format("%s minute%s ", minutes, minutes > 1 ? "s" : "");
        }
        if (seconds > 0) {
            upTime += String.format("%s second%s ", seconds, seconds > 1 ? "s" : "");
        }
        return upTime;
    }

    public void sendToDiscord(String message) {
        final TextChannel channel = this.jda.getTextChannelById(DiscordBot.ChannelID);
        if (channel != null) {
            channel.sendMessage(message).queue();
        }
    }

    public void sendAdvancementMessage(String text) {
        this.sendToDiscord("**\uD83C\uDF8A " + text + "**");
    }

    public void sendDeathMessage(String text) {
        this.sendToDiscord("**\uD83D\uDD71 " + text + "**");
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
            final BaseText symbol = new LiteralText("\u24B9");
            final HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText(member.getUser().getAsTag()));
            final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, channelURL);
            symbol.styled(style -> style
                    .withColor(Formatting.BLUE)
                    .withHoverEvent(hoverEvent)
                    .withClickEvent(clickEvent));
            final BaseText text = new LiteralText("\u02F9" + name + "\u02FC " + content);
            final MutableText ingameMessage = new LiteralText("").append(symbol).append(text);
            for (Message.Attachment file : attachments) {
                final BaseText fileName = new LiteralText(" " + file.getFileName());
                fileName.styled(style -> style
                        .withColor(Formatting.BLUE)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Open attachment")))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, file.getUrl())));
                ingameMessage.append(fileName);
            }
            this.server.execute(() -> this.server.getPlayerManager().broadcast(ingameMessage, MessageType.CHAT, Util.NIL_UUID));
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("tps")) {
            this.server.execute(() -> {
                final double MSPT = MathHelper.average(this.server.lastTickLengths) * 1E-6D;
                final double TPS = 1000D / Math.max(50, MSPT);
                event.reply(String.format("**TPS: %.2f MSPT: %.2f**", TPS, MSPT)).queue();
            });
        } else if (event.getName().equals("online")) {
            this.server.execute(() -> {
                final String[] players = this.server.getPlayerNames();
                final String title = String.format("%d player%s online:", players.length, players.length != 1 ? "s" : "");
                final MessageEmbed.AuthorInfo author = new MessageEmbed.AuthorInfo(authorName, null, authorIcon, null);
                final MessageEmbed embed = new MessageEmbed(null, title, StringUtils.join(players, "\n").replaceAll("_", "\\\\_"), EmbedType.RICH, null, embedColor, null, null, author, null, null, null, null);
                event.replyEmbeds(embed).queue();
            });
        } else if (event.getName().equals("uptime")) {
            event.reply(getUpTime()).queue();
        }
    }
}
