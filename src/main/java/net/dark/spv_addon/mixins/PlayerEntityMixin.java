package net.dark.spv_addon.mixins;

import net.dark.spv_addon.voicechat.SpvAddonVoicechatPlugin;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void onTickMovement(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        // Marque comme "fait du bruit" si le joueur se déplace, saute, etc.
        if (!player.isSpectator() && player.isAlive()) {
            if (player.getVelocity().lengthSquared() > 0.01) {
                SpvAddonVoicechatPlugin.justMadeNoise.add(player.getUuid());
            }
            if (player.isSprinting() || player.isSwimming()) {
                SpvAddonVoicechatPlugin.justMadeNoise.add(player.getUuid());
            }
            if (player.fallDistance > 0.5f) {
                SpvAddonVoicechatPlugin.justMadeNoise.add(player.getUuid());
            }
        }
    }
}
