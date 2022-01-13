package net.dragonbabyfly.discordchatbridge.mixin;

import net.dragonbabyfly.discordchatbridge.DiscordChatBridge;
import net.minecraft.server.filter.TextStream;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayNetworkHandler.class)
public class MixinServerPlayNetworkHandler {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "handleMessage", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/filter/TextStream$Message;getFiltered()Ljava/lang/String;"))
    public void detectChatMessages(TextStream.Message message, CallbackInfo ci, String string) {
        String playerName = "\u02F9`" + this.player.getName().asString() + "`\u02FC ";
        DiscordChatBridge.discord.sendToDiscord(playerName + message.getRaw());
    }
}
