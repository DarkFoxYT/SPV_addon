package net.dark.spv_addon.mixins.blocks;

import com.sp.block.entity.CeilingLightBlockEntity;
import foundry.veil.api.client.render.deferred.light.AreaLight;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CeilingLightBlockEntity.class)
public abstract class CeilingLightBlockEntityMixin {

    @Unique
    AreaLight light;

    @Unique
    float brightness;

    @Unique
    float angle;

    /**
     * Injects additional logic into the tick method at the tail end.
     */
    @Inject(method = "tick(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At("TAIL"))
    private void tickInject(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (world.isClient) {
            if (world.getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)) {
                light.setBrightness(brightness);
                light.setAngle((float) Math.toRadians(angle));
                light.setColor(1.0F, 0.0F, 0.8F);

                System.out.println("[Mixin] Adjusted light properties for Kitty world.");
            }if (world.getRegistryKey().equals(BackroomsLevels.LEVEL188_WORLD_KEY)) {
                light.setBrightness(brightness);
                light.setAngle((float) Math.toRadians(angle));
                light.setColor(1.0F, 1.0F, 1.0F);

                System.out.println("[Mixin] Adjusted light properties for 105 world.");
            }
        }
    }
}
