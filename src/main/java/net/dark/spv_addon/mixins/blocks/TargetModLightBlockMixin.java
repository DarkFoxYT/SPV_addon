package net.dark.spv_addon.mixins.blocks;

import net.dark.spv_addon.api.SanityLightEmitter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

//@Mixin(TargetModLightBlock.class)
public class TargetModLightBlockMixin implements SanityLightEmitter {

    @Override
    public int sPV_addon$getSanityRadius(World world, BlockPos pos) {
        return 10; // or dynamic logic based on block state
    }
}
