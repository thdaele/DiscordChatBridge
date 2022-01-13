package net.dragonbabyfly.discordchatbridge.mixin;

import net.dragonbabyfly.discordchatbridge.DiscordChatBridge;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {
    @Inject(method = "onDeath", locals = LocalCapture.CAPTURE_FAILHARD, at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"))
    private void sendMessage(DamageSource source, CallbackInfo ci, boolean bl, Text text) {
        DiscordChatBridge.discord.sendDeathMessage(text.asTruncatedString(Integer.MAX_VALUE));
    }
}
