package net.dark.spv_addon.mixins.blocks;

import com.sp.clientWrapper.ClientWrapper;
import net.dark.spv_addon.init.BackroomsLevels;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import com.sp.block.entity.FluorescentLightBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWrapper.class)
public abstract class ClientWrapperMixin {

    @Inject(method = "doClientSideTick", at = @At("TAIL"))
    private static void onClientTick(World world, BlockPos pos, BlockState state, FluorescentLightBlockEntity block, CallbackInfo ci) {
        if (!world.isClient) return;


        if (world.getRegistryKey().equals(BackroomsLevels.LEVEL_KITTY_WORLD_KEY)) {
            if (block.pointLight != null) {
                block.pointLight.setColor(0.631f , 0.102f, 0.631f).setRadius(10);
            }
        }
    }
}
