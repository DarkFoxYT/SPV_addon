package net.dark.spv_addon.api;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface SanityLightEmitter {
    int sPV_addon$getSanityRadius(World world, BlockPos pos);


    public static boolean isPlayerNearSanityLight(World world, BlockPos playerPos) {
        int checkRadius = 16; // Define the maximum radius to check
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int dx = -checkRadius; dx <= checkRadius; dx++) {
            for (int dy = -checkRadius; dy <= checkRadius; dy++) {
                for (int dz = -checkRadius; dz <= checkRadius; dz++) {
                    mutablePos.set(playerPos.getX() + dx, playerPos.getY() + dy, playerPos.getZ() + dz);
                    BlockState state = world.getBlockState(mutablePos);
                    Block block = state.getBlock();

                    if (block instanceof SanityLightEmitter) {
                        int radius = ((SanityLightEmitter) block).sPV_addon$getSanityRadius(world, mutablePos);
                        if (mutablePos.getSquaredDistance(playerPos) <= radius * radius) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
