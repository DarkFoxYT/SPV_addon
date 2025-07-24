package net.dark.spv_addon.mixins.player;

import net.dark.spv_addon.cca.DeathTeleportComponent;
import net.dark.spv_addon.world.levels.managers.Level207Manager;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.dark.spv_addon.cca.InitializeComponents.DEATH_TELEPORT;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMixin {
    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Handle Level 207 death transitions (new system)
        Level207Manager.handlePlayerDeath(player);

        // Keep old death teleport system for compatibility
        DeathTeleportComponent comp = DEATH_TELEPORT.get(this);
        if (comp != null) comp.onDeath();
    }
}