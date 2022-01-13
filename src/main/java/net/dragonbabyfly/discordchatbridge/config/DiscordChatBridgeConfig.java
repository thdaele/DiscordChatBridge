package net.dragonbabyfly.discordchatbridge.config;

import com.oroarmor.config.Config;
import com.oroarmor.config.ConfigItemGroup;
import com.oroarmor.config.StringConfigItem;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class DiscordChatBridgeConfig extends Config {
    // Implementation of the following example mod
    // https://github.com/OroArmor/Oro-Config/blob/master/fabric-testmod/src/main/java/com/oroarmor/config/testmod/TestConfig.java
    public static final ConfigItemGroup mainGroup = new GlobalConfig();

    public static final List<ConfigItemGroup> configs = of(mainGroup);

    public DiscordChatBridgeConfig() {
        super(configs, new File(FabricLoader.getInstance().getConfigDir().toFile(), "DiscordChatBridge.json"), "DiscordChatBridge");
    }

    public static class GlobalConfig extends ConfigItemGroup {
        public static final StringConfigItem token = new StringConfigItem("token", "insert bot token here", "token");
        public static final LongConfigItem chatChannelID = new LongConfigItem("chatChannelID", 0L, "chatChannelID");
        public static final LongConfigItem serverID = new LongConfigItem("serverID", 0L, "serverID");

        public GlobalConfig() {
            super(of(token, chatChannelID, serverID), "DiscordBot");
        }
    }
}
