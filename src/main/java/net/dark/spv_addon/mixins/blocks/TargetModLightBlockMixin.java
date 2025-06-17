package net.dark.spv_addon.mixins.blocks;

import net.dark.spv_addon.Additions.api.SanityLightEmitter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//@Mixin(TargetModLightBlock.class)
public class TargetModLightBlockMixin implements SanityLightEmitter {

    @Override
    public int sPV_addon$getSanityRadius(World world, BlockPos pos) {
        return 15; // or dynamic logic based on block state
    }
}
