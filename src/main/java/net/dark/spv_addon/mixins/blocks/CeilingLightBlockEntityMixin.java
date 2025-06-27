package net.dark.spv_addon.mixins.blocks;

import com.sp.block.entity.CeilingLightBlockEntity;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CeilingLightBlockEntity.class)
public abstract class CeilingLightBlockEntityMixin {

    @Shadow
    AreaLight light;

    @Shadow
    float brightness;

    @Shadow
    float angle;

    /**
     * Injects additional logic into the tick method at the tail end.
     */
    @Inject(method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At("TAIL"))
    private void tickInject(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        // Ensure this code only runs on client side
        if (world.isClient) {
            // Check if we're in your custom Kitty world
            if (world.getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)) {
                light.setBrightness(brightness);
                light.setAngle((float) Math.toRadians(angle));
                light.setColor(1.0F, 0.0F, 0.8F); // Adjust color to a warmer tone

                System.out.println("[Mixin] Adjusted light properties for Kitty world.");
            }
        }
    }
}
