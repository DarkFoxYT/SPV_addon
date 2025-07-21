package net.dark.spv_addon.mixins.blocks;

import com.sp.block.entity.EmergencyLightBlockEntity;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EmergencyLightBlockEntity.class)
public class EmergencyLightBlockEntityMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true, remap = false)
    private void spv_preventTickInCertainDimensions(World world, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (world.isClient && world.getRegistryKey().equals(BackroomsLevels.LEVEL105_WORLD_KEY)) {
            ci.cancel();
        }
    }
}

