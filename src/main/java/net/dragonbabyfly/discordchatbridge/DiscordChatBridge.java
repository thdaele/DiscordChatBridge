package net.dragonbabyfly.discordchatbridge;

import com.oroarmor.config.Config;
import net.dragonbabyfly.discordchatbridge.config.DiscordChatBridgeConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.PlayerManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;

public class DiscordChatBridge implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger("DiscordChatBridge");
    public static final Config CONFIG = new DiscordChatBridgeConfig();
    public static DiscordBot discord = null;

    @Override
    public void onInitialize() {
        CONFIG.readConfigFromFile();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            PlayerManager playerManager = server.getPlayerManager();

            LOGGER.info("Mod started!");
            String token = CONFIG.getValue("DiscordBot.token", String.class);
            try {
                discord = new DiscordBot(token, playerManager);
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
