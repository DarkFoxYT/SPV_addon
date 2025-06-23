package net.dark.spv_addon.mixins.blocks;

import com.sp.block.custom.EmergencyLightBlock;
import net.dark.spv_addon.Additions.api.SanityLightEmitter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EmergencyLightBlock.class)
public class EmergencyLightBlockMixin implements SanityLightEmitter {

    @Override
    public int sPV_addon$getSanityRadius(World world, BlockPos pos) {
        return 10;
    }
}
