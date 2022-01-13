package net.dragonbabyfly.discordchatbridge.mixin;

import net.dragonbabyfly.discordchatbridge.DiscordChatBridge;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public abstract class MixinTameableEntity extends AnimalEntity {
    protected MixinTameableEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;sendSystemMessage(Lnet/minecraft/text/Text;Ljava/util/UUID;)V"))
    private void sendMessage(DamageSource source, CallbackInfo ci) {
        DiscordChatBridge.discord.sendDeathMessage(this.getDamageTracker().getDeathMessage().asTruncatedString(Integer.MAX_VALUE));
    }
}
