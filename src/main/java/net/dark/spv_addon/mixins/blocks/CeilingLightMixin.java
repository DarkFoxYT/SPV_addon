
package net.dark.spv_addon.mixins.blocks;

import com.sp.block.custom.CeilingLight;
import net.dark.spv_addon.api.SanityLightEmitter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CeilingLight.class)
public class CeilingLightMixin implements SanityLightEmitter {

    @Override
    public int sPV_addon$getSanityRadius(World world, BlockPos pos) {
        return 15;
    }
}
