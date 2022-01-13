package net.dragonbabyfly.discordchatbridge.mixin;

import net.dragonbabyfly.discordchatbridge.DiscordChatBridge;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.network.MessageType;
import net.minecraft.server.PlayerManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.UUID;

@Mixin(PlayerAdvancementTracker.class)
public class MixinPlayerAdvancementTracker {
    @Redirect(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"))
    private void sendAdvancement(PlayerManager instance, Text message, MessageType type, UUID sender) {
        instance.broadcast(message, type, sender);
        DiscordChatBridge.discord.sendAdvancementMessage(message.asTruncatedString(Integer.MAX_VALUE));
    }
}
