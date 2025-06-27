package net.dark.spv_addon.mixins;

import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
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
        if (!player.isSpectator() && player.isAlive()) {
            boolean madeNoise = player.getVelocity().lengthSquared() > 0.1
                    || player.isSprinting()
                    || player.isCrawling()
                    || player.isUsingItem()
                    || player.isClimbing()
                    || player.isTouchingWater()
                    || player.isOnFire()
                    || player.isSwimming()
                    || player.fallDistance > 0.5f;
            if (madeNoise) {
                SpvAddonVoicechatPlugin.justMadeNoise.add(player.getUuid());
            }
        }
    }
}

