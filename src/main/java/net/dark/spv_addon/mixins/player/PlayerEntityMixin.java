package net.dark.spv_addon.mixins.player;

import net.dark.spv_addon.init.voicechat.VoiceActivityTracker;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) {
            return;
        }

        if (!player.isSpectator() && player.isAlive()) {
            float noise = 0.0f;
            if (player.getVelocity().horizontalLengthSquared() > 0.08) noise += 0.35f;
            if (player.isSprinting()) noise += 0.45f;
            if (player.isCrawling()) noise += 0.20f;
            if (player.isUsingItem()) noise += 0.15f;
            if (player.isClimbing()) noise += 0.25f;
            if (player.isTouchingWater()) noise += 0.10f;
            if (player.isOnFire()) noise += 0.55f;
            if (player.isSwimming()) noise += 0.20f;
            if (player.fallDistance > 0.5f) noise += 0.30f;

            if (noise > 0.0f) {
                VoiceActivityTracker.recordMovementNoise(player, Math.min(1.0f, noise));
            }
        }
    }
}

